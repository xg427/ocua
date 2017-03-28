import style from "./style.less";
import cn from "classnames";
import URI from "urijs";
import {fetchJson} from "../tools";

const ROLE_ADMIN = 'ROLE_ADMIN';

class Filter extends React.Component{
  render(){
    const {title, open, onClick} = this.props;
    return(
      <div onClick={onClick} className={cn('filter', {open})}>
        {title}
        <i className="glyphicon glyphicon-menu-down"></i>
        {open && <div className="dropdown">
        </div>}
      </div>
    )
  }
}

class Filters extends React.Component{
  render(){
    const {box, requestNewFilterBox} = this.props;
    const setBox = box => e => {
      e.stopPropagation();
      requestNewFilterBox(box);
    }

    const filters = [{
      title: "Organizations",
      slug:  "organizations"
    },{
      title: "Procurement method",
      slug:  "procurementMethod"
    },{
      title: "Value Amount",
      slug:  "valueAmount"
    },{
      title: "Date",
      slug:  "date"
    },{
      title: "Location",
      slug:  "location"
    }];
    return (
      <div className="row filters-bar">
        <div className="col-lg-3 col-md-2 col-sm-1 title text-right">
          Filter your data
        </div>
        <div className="col-lg-7 col-md-9 col-sm-10">
	      {filters.map(({title, slug}, index) => (
  	      <Filter
    	    	title={title}
	        	open={box == slug}
		        onClick={setBox(slug)}
  	        key={index}
    	    />
        ))}
        </div>
        <div className="col-lg-2 col-md-1 col-sm-1 download">
          <button className="btn btn-success">
            <i className="glyphicon glyphicon-download-alt"></i>
          </button>
        </div>
      </div>
    )
  }
}

import Chart from "../visualizations/charts/index.jsx";
import Plotly from "plotly.js/lib/core";
Plotly.register([
  require('plotly.js/lib/pie')
]);

class TotalFlags extends Chart{
  constructor(...args){
    super(...args);
    this.state = {
      
    }
  }

  getData(){
    return [{
      values: [22565, 5450, 5850],
      labels: ["Fraud", "Process rigging", "Collusion"],
      textinfo: 'value',
      hole: .85,
      type: 'pie'
    }];
  }

  hasNoData(){
    return false;
  }

  getLayout(){
    const {width} = this.props;
    console.log(this.props);
    return {
      legend: {
        orientation: 'h',
        width,
        height: 50,
        x: '0',
        y: '0'
      },
      paper_bgcolor: 'rgba(0,0,0,0)'
    }
  }
}

import {Map} from "immutable";


class CorruptionRiskDashboard extends React.Component{
  constructor(...args){
    super(...args);
    this.state={
      dashboardSwitcherOpen: false,
      filterBox: "",
      user: {
        loggedIn: false,
        isAdmin: false
      }
    }
  }

  fetchUserInfo(){
    const noCacheUrl = new URI('/rest/userDashboards/getCurrentAuthenticatedUserDetails').addSearch('time', Date.now());
    fetchJson(noCacheUrl).then(
      ({username, id, roles}) => this.setState({
        user: {
          loggedIn: true,
          isAdmin: roles.some(({authority}) => authority == ROLE_ADMIN),
          id
        }
      })
    ).catch(
      () => this.setState({
        user: {
          loggedIn: false
        }
      })
    )
  }

  componentDidMount(){
    this.fetchUserInfo();
  }

  toggleDashboardSwitcher(e){
    e.stopPropagation();
    const {dashboardSwitcherOpen} = this.state;
    this.setState({dashboardSwitcherOpen: !dashboardSwitcherOpen});
  }

  loginBox(){
    if(this.state.user.loggedIn){
      return <a href="/preLogout?referrer=/ui/index.html">
				<button className="btn btn-success">Logout</button>
      </a>
    }
    return <a href="/login?referrer=/ui/index.html">
        <button className="btn btn-success">Login</button>
    </a>
  }

  render(){
    const {dashboardSwitcherOpen, filterBox} = this.state;
    const {onSwitch} = this.props;
    const tabs = [{
	    slug: "fraud",
	    name: "Fraud"
    }, {
	    slug: "process_rigging",
	    name: "Process rigging"
    }, {
	    slug: "collusion",
	    name: "Collusion"
    }];
    return (
      <div className="container-fluid dashboard-corruption-risk"
           onClick={e => this.setState({dashboardSwitcherOpen: false, filterBox: ""})}
      >
        <header className="branding row">
          <div className="col-sm-1 logo-wrapper">
            <img src="assets/logo.png"/>
          </div>
          <div className="col-sm-8">
            <div className={cn('dash-switcher-wrapper', {open: dashboardSwitcherOpen})}>
              <h1 onClick={this.toggleDashboardSwitcher.bind(this)}>
                Corruption Risk Dashboard
                <i className="glyphicon glyphicon-menu-down"></i>
              </h1>
              {dashboardSwitcherOpen &&
               <div className="dashboard-switcher">
                 <a href="javascript:void(0);" onClick={e => onSwitch('default')}>
                   Default dashboard
                 </a>
               </div>
              }
            </div>
          </div>
          <div className="col-sm-2 login-wrapper">
            {this.loginBox()}
          </div>
          <div className="col-sm-1">
          </div>
        </header>
        <Filters box={filterBox} requestNewFilterBox={filterBox => this.setState({filterBox})}/>
        <aside className="col-xs-4 col-md-3 col-lg-2">
          <h4>
            Corruption Risk Overview
            <i className="glyphicon glyphicon-info-sign"></i>
          </h4>
          <p>
            <small>
              The Corruption Risk Dashboard employs a
              red flagging approach to help users understand
              the potential presence of fraud, collusion or
              rigging in public contracting. White flags may
              indicate the presence of corruption, they may
              also be attributable to data quality issues or
              approved practices.
            </small>
          </p>
          <section role="navigation" className="row">
            {tabs.map(({name, slug}, index) =>
              <a
                  href="javascript:void(0);"
                  onClick={e => null} className={cn({active: "fraud" == slug})}
                  key={index}
              >
                <img src={`assets/icons/${slug}.png`}/>
                {name} <span className="count">(0)</span>
              </a>
						 )}
          </section>
          <TotalFlags
              filters={Map()}
              requestNewData={e => null}
              translations={{}}
              data={Map({a: 1})}
              width={250}
              height={300}
              margin={{l:40, r:40, t:40, b: 40, pad:40}}
          />
        </aside>
      </div>
    )
  }
}

export default CorruptionRiskDashboard;