import Tab from "./index";
import BidPeriod from "../visualizations/charts/bid-period";
import Cancelled from "../visualizations/charts/cancelled";
import NrCancelled from "../visualizations/charts/nr-cancelled";
import BidsByItem from "../visualizations/charts/bids-by-item";

class Efficiency extends Tab{
  static getName(t){return t('tabs:efficiency:title')}
}

Efficiency.icon = "efficiency";
Efficiency.visualizations = [BidPeriod, BidsByItem, Cancelled, NrCancelled];

export default Efficiency;
