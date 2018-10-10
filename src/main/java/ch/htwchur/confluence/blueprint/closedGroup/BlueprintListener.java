package ch.htwchur.confluence.blueprint.closedGroup;

import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;

import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.user.Group;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class BlueprintListener implements InitializingBean, DisposableBean {

	@ConfluenceImport
	private EventPublisher eventPublisher;

	@ConfluenceImport
	UserAccessor userAccessor;

	private static final Logger log = LoggerFactory.getLogger(BlueprintListener.class);
	private static final String userFieldKey = "userMultiField";
	private static final String spaceAdminField = "userMultiField2";
	 public static final String MODULE_COMPLETE_KEY = "ch.htwchur.confluence.blueprint.closedGroup.htw_closedGroup_blueprint:closedGroup-blueprint";
	@Inject
	public BlueprintListener(EventPublisher eventPublisher,UserAccessor userAccessor) {
		this.eventPublisher = eventPublisher;
		this.userAccessor = userAccessor;
	}

	@EventListener
	public void onBlueprintCreateEvent(BlueprintPageCreateEvent event) {

	}


	@EventListener
	public void onBlueprintCreateEvent(SpaceBlueprintCreateEvent event) {

		Space space = event.getSpace();
		
	
		
	if(MODULE_COMPLETE_KEY.equals(event.getSpaceBlueprint().getModuleCompleteKey())){

		
		// permissionManager.createDefaultSpacePermissions(event.getSpace());
					List<SpacePermission> permissions = space.getPermissions();

					 List<SpacePermission> permissionToRemove = new ArrayList<SpacePermission>();
					 
					// remove editspace for confluence-usres
					for (SpacePermission perm : permissions) {
						if (perm.getGroup() == "confluence-users") {
							permissionToRemove.add(perm);

						}
					}
					
					for(SpacePermission permToRemov : permissionToRemove) {
					space.removePermission(permToRemov);
					}
					
		
		if (event.getContext().containsKey(userFieldKey)) {

			String usersFieldContent = event.getContext().get(userFieldKey).toString();
			String[] users = usersFieldContent.split(",");

			List<String> permissionsToGiveToUser = new ArrayList<String>();
			permissionsToGiveToUser.add(SpacePermission.CREATEEDIT_PAGE_PERMISSION);
			permissionsToGiveToUser.add(SpacePermission.VIEWSPACE_PERMISSION);
			permissionsToGiveToUser.add(SpacePermission.EDITBLOG_PERMISSION);
			permissionsToGiveToUser.add(SpacePermission.REMOVE_OWN_CONTENT_PERMISSION);
			permissionsToGiveToUser.add(SpacePermission.EXPORT_SPACE_PERMISSION);
			permissionsToGiveToUser.add(SpacePermission.COMMENT_PERMISSION);
			permissionsToGiveToUser.add(SpacePermission.CREATE_ATTACHMENT_PERMISSION);


			for (String username : users) {

				for (String permission : permissionsToGiveToUser) {
					ConfluenceUser confluenceUser = userAccessor.getUserByName(username);
					SpacePermission spacePerm = SpacePermission.createUserSpacePermission(permission, space, confluenceUser);
					space.addPermission(spacePerm);
				}

			}

			

		}
		
		if (event.getContext().containsKey(spaceAdminField)) {

			String adminFieldContent = event.getContext().get(spaceAdminField).toString();
			String[] admins = adminFieldContent.split(",");

			List<String> permissionsToGiveToAdmin = new ArrayList<String>();
			permissionsToGiveToAdmin.add(SpacePermission.CREATEEDIT_PAGE_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.VIEWSPACE_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.EDITBLOG_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.REMOVE_OWN_CONTENT_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.EXPORT_SPACE_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.COMMENT_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.REMOVE_PAGE_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.REMOVE_BLOG_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.REMOVE_ATTACHMENT_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.CREATE_ATTACHMENT_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.REMOVE_COMMENT_PERMISSION);
			permissionsToGiveToAdmin.add(SpacePermission.ADMINISTER_SPACE_PERMISSION);



			for (String username : admins) {

				for (String permission : permissionsToGiveToAdmin) {
					ConfluenceUser admin = userAccessor.getUserByName(username);
					SpacePermission spacePerm = SpacePermission.createUserSpacePermission(permission, space, admin);
					space.addPermission(spacePerm);
				}

			}

			

		}
	}

	}

	@EventListener
	public void onBlueprintCreateEvent(SpaceBlueprintHomePageCreateEvent event) {

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
	}

	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

}
