package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;
import java.util.List;
import org.devgateway.ocds.persistence.mongo.flags.FlagType;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.ocds.web.spring.ReleaseFlaggingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author mpostelnicu
 * @since 01/13/2017
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class TotalFlagsControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private TotalFlagsController totalFlagsController;


    @Autowired
    private ReleaseFlaggingService releaseFlaggingService;

    public static void logMessage(String message) {
        logger.info(message);
    }

    @Before
    public final void setUp() throws Exception {
        super.setUp();
        releaseFlaggingService.processAndSaveFlagsForAllReleases(ReleaseFlaggingServiceTest::logMessage);
    }

    @Test
    public void totalFlagsByIndicatorTypeTest() throws Exception {
        final List<DBObject> result = totalFlagsController
                .totalFlaggedIndicatorsByIndicatorType(new YearFilterPagingRequest());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(FlagType.RIGGING.toString(), result.get(0).get(TotalFlagsController.Keys.TYPE));
        Assert.assertEquals(2, result.get(0).get(TotalFlagsController.Keys.COUNT));
    }

    @Test
    public void totalFlagsTest() throws Exception {
        final List<DBObject> result = totalFlagsController
                .totalFlaggedIndicators(new YearFilterPagingRequest());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(2, result.get(0).get(TotalFlagsController.Keys.COUNT));
    }

}
