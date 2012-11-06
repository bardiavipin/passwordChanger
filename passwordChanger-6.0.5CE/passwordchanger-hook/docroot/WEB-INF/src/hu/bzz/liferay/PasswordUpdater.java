package hu.bzz.liferay;

import java.util.Date;
import java.util.Properties;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.persistence.CompanyUtil;

public class PasswordUpdater extends SimpleAction {

	@Override
	public void run(String[] arg0) throws ActionException {
		Properties props = new Properties();
		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream("password.changer.properties"));
			String type = props.getProperty("type");
			String virtualHost = props.getProperty("virtualhost"); 
			Company c = CompanyUtil.fetchByVirtualHost(virtualHost);
			User u = null;
			String name = null;
			if ("screenname".equals(type)) {
				String screenName = props.getProperty("screenname");
				u = UserLocalServiceUtil.getUserByScreenName(c.getCompanyId(), screenName);
				name = screenName;
			} else if ("e-mail".equals(type)) {
				String emailAddress = props.getProperty("emailaddress");
				u = UserLocalServiceUtil.getUserByEmailAddress(c.getCompanyId(), emailAddress);
				name = emailAddress;
			}
			else {
				_log.error("You should set type to screenname or e-mail if you want to use the password updater.");
			}
			String password = props.getProperty("password");
			UserLocalServiceUtil.updatePasswordManually(u.getUserId(), password, false, true, new Date());
			_log.info("Password for " + name + " was updated.");
		} catch (Exception e) {
			_log.error(e);
		}
	}
	
	private static Log _log = LogFactoryUtil.getLog(PasswordUpdater.class);

}
