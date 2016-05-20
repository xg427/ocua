/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.spring;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListReportProvider;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * @author mpostelnicu
 *
 */
public class OcdsSchemaValidation {
	private final Logger logger = LoggerFactory.getLogger(OcdsSchemaValidation.class);
	private JsonSchema schema;

	public static final String OCDS_1_0_SCHEMA_LOCATION = "/release-schema.json";
	public static final String OCDS_1_0_LOCATION_PATCH_LOCATION = "/location_patch_schema.json";

	private ObjectMapper jacksonObjectMapper;
	private String[] patchResourceNames;

	public class ProcessingReportWithNode {
		private ProcessingReport report;
		private String failedValidationObjString;

		public ProcessingReportWithNode(ProcessingReport report, String failedValidationObjString) {
			this.report = report;
			this.failedValidationObjString = failedValidationObjString;
		}

		public ProcessingReport getReport() {
			return report;
		}

		public String getFailedValidationObjString() {
			return failedValidationObjString;
		}

		@Override
		public String toString() {
			return report.toString() + " FAILED OBJECT JSON: " + failedValidationObjString;
		}
	}

	public OcdsSchemaValidation(ObjectMapper jacksonObjectMapper) {

		this.jacksonObjectMapper = jacksonObjectMapper;

	}

	public OcdsSchemaValidation withJsonPatches(String... patchResourceNames) {
		this.patchResourceNames = patchResourceNames;
		return this;
	}

	public void init() {

		try {

			JsonNode ocdsSchemaNode = JsonLoader.fromResource(OCDS_1_0_SCHEMA_LOCATION);

			if (patchResourceNames != null && patchResourceNames.length > 0) {
				for (int i = 0; i < patchResourceNames.length; i++) {
					JsonNode locationPatchNode = JsonLoader.fromResource(patchResourceNames[i]);
					JsonPatch locationPatch = JsonPatch.fromJson(locationPatchNode);
					ocdsSchemaNode = locationPatch.apply(ocdsSchemaNode);
				}
			}

			logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ocdsSchemaNode));

			schema = JsonSchemaFactory.newBuilder()
					.setReportProvider(new ListReportProvider(LogLevel.ERROR, LogLevel.FATAL)).freeze()
					.getJsonSchema(ocdsSchemaNode);

		} catch (ProcessingException | IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (JsonPatchException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public ProcessingReportWithNode validate(final JsonNode jsonNode) {
		try {
			ProcessingReport processingReport = schema.validate(jsonNode);
			ProcessingReportWithNode processingReportWithNode = null;
			try {
				processingReportWithNode = new ProcessingReportWithNode(processingReport, processingReport.isSuccess()
						? null : jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
			} catch (JsonProcessingException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			return processingReportWithNode;
		} catch (ProcessingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public ProcessingReportWithNode validate(final Object object) {
		JsonNode value = jacksonObjectMapper.convertValue(object, JsonNode.class);
		return validate(value);
	}

	public <S> List<ProcessingReportWithNode> validateAll(final Collection<S> values) {
		return values.stream().map(this::validate).collect(Collectors.toList());
	}

}