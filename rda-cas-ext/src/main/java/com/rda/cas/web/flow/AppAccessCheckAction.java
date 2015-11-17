/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.cas.web.flow;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;


/**
 * <P>
 * 此action插入到cas login flow中的generateServiceTicket之前,<br/>
 * 在生成ST之前判断此用户是否有权限访问此应用程序,如果无则给出提示页面。</br>
 * </P>
 * @author lianrao
 * @since  0.0.1
 */
public class AppAccessCheckAction extends AbstractAction {

	@NotNull
	private TicketRegistry ticketRegistry;

	@NotNull
	private JdbcTemplate jdbcTemplate;

	@NotNull
	private DataSource dataSource;

	/* (non-Javadoc)
	 * @see org.springframework.webflow.action.AbstractAction#doExecute(org.springframework.webflow.execution.RequestContext)
	 * @author lianrao
	 */
	@Override
	protected Event doExecute(RequestContext context) throws Exception {
		//get principal 
		final String ticketGrantingTicket = WebUtils.getTicketGrantingTicketId(context);
		Ticket ticket = ticketRegistry.getTicket(ticketGrantingTicket);
		String username = "";
		if (TicketGrantingTicketImpl.class.isAssignableFrom(ticket.getClass())) {
			Authentication authentication = ((TicketGrantingTicketImpl) ticket).getAuthentication();
			Principal principal = authentication.getPrincipal();
			username = principal.getId();
		}

		//get service
		WebApplicationService service = WebUtils.getService(context);
		String serviceId = service.getId();

		//check app privilege
		if (!isGranted2AccessThisService(username, serviceId)) {
			return no();
		}
		return yes();
	}

	/**
	 * <p>判断是否有权限访问对应应用</p>
	 * @param username
	 * @param serviceId
	 * @return
	 * @author lianrao
	 */
	private boolean isGranted2AccessThisService(String username, String serviceId) {
		logger.debug("check user , service Id :" + username +"," + serviceId);
		String sql = "select count(*) from User, User_Granted_Services "
				+ "where login_name = ? and id = user_id and service_id = ?";
		Integer res = jdbcTemplate.queryForObject(sql, new Object[] { username, serviceId }, Integer.class);
		return res.intValue() > 0 ? true : false;
	}

	/**
	 * @return the ticketRegistry
	 */
	public TicketRegistry getTicketRegistry() {
		return ticketRegistry;
	}

	/**
	 * @param ticketRegistry the ticketRegistry to set
	 */
	public void setTicketRegistry(TicketRegistry ticketRegistry) {
		this.ticketRegistry = ticketRegistry;
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

}
