AJS.bind("blueprint.wizard-register.ready", function () {
	function submitClosedGroup(e, state) {
	    state.pageData.ContentPageTitle = state.pageData.name;
	    return Confluence.SpaceBlueprint.CommonWizardBindings.submit(e, state);
	}
	function preRenderClosedGroup(e, state) {
	    state.soyRenderContext['atlToken'] = AJS.Meta.get('atl-token');
	    state.soyRenderContext['showSpacePermission'] = false;
	}
	
	Confluence.Blueprint.setWizard('ch.htwchur.confluence.blueprint.closedGroup.htw_closedGroup_blueprint:closedGroup-blueprint-item', function(wizard) {
	    wizard.on("submit.closedGroupId", submitClosedGroup);
	    wizard.on("pre-render.closedGroupId", preRenderClosedGroup);
	    wizard.on("post-render.closedGroupId", Confluence.SpaceBlueprint.CommonWizardBindings.postRender);
	    
	});
});




