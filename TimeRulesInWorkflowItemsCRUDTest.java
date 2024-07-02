public class WorkflowAutoTaskAddComment extends AutomationTest {
    protected Workflow.Data workflow;
    protected WorkflowStage.Data stage;
    protected WorkflowAutoTask.Data autoTask1, autoTask2, autoTask3, autoTask4;
    private Bug.Data bug1, bug2, bug3;
    private UseCase.Data useCase;
    private QualitySpecification.Data qualitySpecification;
    private Functionality.Data functionality;
    private Distribution.Data distribution, release2;
    private static final String comment = "Test comment";

    @Before
    public void initialize() {
        workflow = Workflow.Rest.create();
        stage = WorkflowStage.Rest.createParallel(workflow);
        distribution = Distribution.Rest.create();
        release2 = Distribution.Rest.create();
    }

    @Test
    public void addCommentAutoTaskCRUD() {
        navigatelToEntity(stage);
        reporter.testStep("Add Auto Task - Add Comment");
        String name1 = "autoTask_AddComment_" + RandomStringUtils.randomAlphabetic(5);
        UI.Views.workflowStageView.stageItemsContainer.toolbar.openToolbarAddAutoTaskDialog();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.expectFieldNotExist(WorkflowAutoTask.Fields.TO_UPDATE);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.expectFieldNotExist(WorkflowAutoTask.Fields.COMMENT);//
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setField(WorkflowAutoTask.Fields.NAME, name1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setActionType(WorkflowAutoTaskType.ADD_COMMENT);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.expectTextToContain("Comment");
        reporter.testStep("Set Bug as entity type with 3 filters and set comment to update");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.customizeFields.clickShowRequiredOrIncorrectFieldsButton();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setEntityTypeToUpdate(Bug.ENTITY_TYPE);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.filter.clickAddFilter();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.filter.filterField(Bug.Fields.SEVERITY)
                .addFilter(FilterOperator.EQUAL_TO_IN, Collections.singletonList(Severity.LOW.getEntity()));
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.filter.clickAddButton(FilterPanel.ButtonType.AND);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.filter.filterField(Bug.Fields.METAPHASE)
                .addFilter(FilterOperator.NOT_EQUAL_TO_NOT_IN, Collections.singletonList(MetaphasePhase.DONE.getEntityData()));
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.filter.clickAddButton(FilterPanel.ButtonType.AND);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.filter.filterField(Bug.Fields.PRIORITY)
                .addFilter(FilterOperator.EQUAL_TO_IN, Collections.singletonList(Priority.HIGH.getEntity()));
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setComment(comment);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAddAnother();
        reporter.testStep("Check that autoTask is created and new add form for Add Comment is opened");
        GeneralUtils.delay(3000);
        WorkflowAutoTask.Data autoTask1 = WorkflowAutoTask.Rest.getEntityWithFields(name1);
        assertEquals("Wrong phase autoTask: ", WorkflowAutoTaskPhase.PLANNED.getValue(), autoTask1.getPhaseName());
        assertEquals("autoTask has the wrong action type: ", WorkflowAutoTaskType.ADD_COMMENT.getValue(), autoTask1.getActionType());
        String name2 = "autoTask_AddComment_" + RandomStringUtils.randomAlphabetic(5);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, ACTION_TYPE, WorkflowAutoTaskType.ADD_COMMENT.getLabel());
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setField(WorkflowAutoTask.Fields.NAME, name2);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.customizeFields.clickShowRequiredOrIncorrectFieldsButton();
        reporter.testStep("Set all required fields");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setEntityTypeToUpdate("Quality Specification");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.filter.clickAddFilter();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.filter.filterField(QualitySpecification.Fields.PHASE)
                .addFilter(FilterOperator.EQUAL_TO_IN, Collections.singletonList(QualityStoryPhase.NEW.getEntityData()));
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setComment(comment);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        GeneralUtils.delay(3000);
        WorkflowAutoTask.Data autoTask2 = WorkflowAutoTask.Rest.getEntityWithFields(name2);
        assertEquals("Wrong phase autoTask: ", WorkflowAutoTaskPhase.PLANNED.getValue(), autoTask2.getPhaseName());
        assertEquals("autoTask has the wrong action type: ", WorkflowAutoTaskType.ADD_COMMENT.getValue(), autoTask2.getActionType());
        reporter.info("Workflow contains two autoTask for Add comment");
        navigatelToEntity(autoTask1);
        WorkflowAutoTaskView.clickDetailsTab();
        reporter.testStep("View and edit autoTask fields");
        WorkflowAutoTaskView.details.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, ACTION_TYPE, WorkflowAutoTaskType.ADD_COMMENT.getLabel());
        WorkflowAutoTaskView.details.filter.expectFilterExists(Bug.Fields.SEVERITY);
        WorkflowAutoTaskView.details.filter.expectFilterExists(Bug.Fields.METAPHASE);
        WorkflowAutoTaskView.details.filter.expectFilterExists(Bug.Fields.PRIORITY);
        reporter.testStep("Change Entity Type from bug to user story");
        WorkflowAutoTaskView.details.setEntityTypeToUpdate(UseCase.ENTITY_TYPE);
        reporter.testStep("Check that filter become empty");
        WorkflowAutoTaskView.details.filter.expectFilterNotExists(Bug.Fields.SEVERITY);
        WorkflowAutoTaskView.details.filter.expectFilterNotExists(Bug.Fields.METAPHASE);
        WorkflowAutoTaskView.details.filter.expectFilterNotExists(Bug.Fields.PRIORITY);
        reporter.testStep("Add new filter and save autoTask");
        WorkflowAutoTaskView.details.setComment("" + "\b");
        WorkflowAutoTaskView.details.customizeFields.clickShowRequiredOrIncorrectFieldsButton();
        WorkflowAutoTaskView.details.filter.clickAddFilter();
        WorkflowAutoTaskView.details.filter.filterField(UseCase.Fields.PRIORITY)
                .addFilter(FilterOperator.EQUAL_TO_IN, Collections.singletonList(Priority.MEDIUM.getEntity()));
        WorkflowAutoTaskView.details.setComment(comment);
        WorkflowAutoTaskView.details.toolbar.clickSave();
        WorkflowAutoTaskView.details.toolbar.saveButton.expectToBeDisable();
        reporter.testStep("Delete autoTask");
        WorkflowAutoTaskView.details.toolbar.clickDelete();
        UI.dialogs.warningMessageDialog.clickDelete();
        navigatelToEntity(workflow);
        UI.views.workflowView.workflowItemsContainer.grid.expectNotVisible(autoTask1);
        reporter.info("autoTask is deleted");
    }

    @Test
    public void executeAddCommentAutoTask() {
        prepareData();
        Workflow.Rest.startWorkflow(workflow);
        navigatelToEntity(workflow);
        reporter.testStep("Check that if API key is not defined in Workflow then autoTask should be in the Failed phase with the corresponding execution message");
        waitingPhase(autoTask1, WorkflowAutoTaskPhase.FAILED.getValue());
        waitingPhase(autoTask2, WorkflowAutoTaskPhase.FAILED.getValue());
        waitingPhase(autoTask3, WorkflowAutoTaskPhase.FAILED.getValue());
        waitingPhase(autoTask4, WorkflowAutoTaskPhase.FAILED.getValue());
        UI.views.workflowView.workflowItemsContainer.toolbar.addColumns(MESSAGE);
        UI.views.workflowView.workflowItemsContainer.grid.getItemField(autoTask1, MESSAGE).expectValueToBe(WorkflowAutoTask.Message.API_KEY_NOT_DEFINED.getValue());
        UI.views.workflowView.workflowItemsContainer.grid.getItemField(autoTask2, MESSAGE).expectValueToBe(WorkflowAutoTask.Message.API_KEY_NOT_DEFINED.getValue());
        UI.views.workflowView.workflowItemsContainer.grid.getItemField(autoTask3, MESSAGE).expectValueToBe(WorkflowAutoTask.Message.API_KEY_NOT_DEFINED.getValue());
        UI.views.workflowView.workflowItemsContainer.grid.getItemField(autoTask4, MESSAGE).expectValueToBe(WorkflowAutoTask.Message.API_KEY_NOT_DEFINED.getValue());
        setAPIUserToWorkflow();
        reporter.info("Api key user is defined in Workflow");
        reporter.testStep("Retry AAs and check that they completed with the corresponding execution message");
        retryAutoTaskWithAPIUserAndCheck(autoTask1, 2);
        retryAutoTaskWithAPIUserAndCheck(autoTask2, 1);
        retryAutoTaskWithAPIUserAndCheck(autoTask3, 1);
        retryAutoTaskWithAPIUserAndCheck(autoTask4, 1);
        reporter.testStep("Check that US, QS, Bug, Functionality which matches filter in autoTask, contains comments from autoTask");
        navigatelToEntity(bug1);
        UI.Views.bugView.clickDetailsTab();
        UI.Views.bugView.openCommentPaneIfNeed();
        UI.Views.bugView.commentPane.getLine(0).expectContainText(comment);
        navigatelToEntity(bug2);
        UI.Views.bugView.clickDetailsTab();
        UI.Views.bugView.openCommentPaneIfNeed();
        UI.Views.bugView.commentPane.getLine(0).expectContainText(comment);
        navigatelToEntity(bug3);
        UI.Views.bugView.clickDetailsTab();
        UI.Views.bugView.openCommentPaneIfNeed();
        UI.Views.bugView.commentPane.expectNoComments();
        navigatelToEntity(qualitySpecification);
        UI.Views.qualityStoryView.clickDetailsTab();
        UI.Views.qualityStoryView.openCommentPaneIfNeed();
        UI.Views.qualityStoryView.commentPane.getLine(0).expectContainText(comment);
        navigatelToEntity(useCase);
        UI.Views.userStoryView.clickDetailsTab();
        UI.Views.userStoryView.openCommentPaneIfNeed();
        UI.Views.userStoryView.commentPane.getLine(0).expectContainText(comment);
        navigatelToEntity(functionality);
        UI.Views.featureDocView.clickDetailsTab();
        UI.Views.featureDocView.openCommentPaneIfNeed();
        UI.Views.featureDocView.commentPane.getLine(0).expectContainText(comment);

    }

    @Test
    public void executeAddCommentAA() {
        prepareData2();
        Workflow.Rest.startWorkflow(workflow);
        waitingPhase(autoTask1, WorkflowAutoTaskPhase.COMPLETED.getValue());
        navigatelToEntity(autoTask1);
        WorkflowAutoTaskView.clickDetailsTab();
        reporter.testStep("Check if the items do not match the autoTask filters, then autoTask will succeed, but will not update any items");
        WorkflowAutoTaskView.details.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, WorkflowAutoTask.Fields.MESSAGE, WorkflowAutoTask.Message.ADD_COMMENT_ITEMS_NOT_COMPLY.getValue());
        navigatelToEntity(useCase);
        UI.Views.userStoryView.clickDetailsTab();
        UI.Views.userStoryView.openCommentPaneIfNeed();
        UI.Views.userStoryView.commentPane.expectNoComments();
    }

    @Test
    public void updateWorkflowViaAddCommentAA() {
        setAPIUserToWorkflow();
        navigatelToEntity(stage);
        reporter.testStep("Add Auto Task - Add Comment with Entity type = \"This Workflow\"");
        String thisWorkflow = "This Workflow";
        String name = "addComment_ThisWorkflow_" + RandomStringUtils.randomAlphabetic(5);
        UI.Views.workflowStageView.stageItemsContainer.toolbar.openToolbarAddAutoTaskDialog();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setField(WorkflowAutoTask.Fields.NAME, name);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setActionType(WorkflowAutoTaskType.ADD_COMMENT);
        reporter.testStep("Choose option 'This Workflow' for Entity type field");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setEntityTypeToUpdate(thisWorkflow);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.expectFilterDisabled();
        reporter.info("Filter should be disabled with 'N/A' text");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.details.setComment(comment);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        GeneralUtils.delay(3000);
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.getEntityWithFields(name);
        reporter.testStep("Execute autoTask and verify that a new comment is added to the current Workflow");
        Workflow.Rest.startWorkflow(workflow);
        waitingPhase(autoTask, WorkflowAutoTaskPhase.COMPLETED.getValue());
        navigatelToEntity(workflow);
        UI.views.workflowView.clickDetailsTab();
        UI.views.workflowView.openCommentPaneIfNeed();
        UI.views.workflowView.commentPane.getLine(0).expectContainText(comment);
    }

    private void setAPIUserToWorkflow() {
        workflow = Workflow.Rest.update(workflow, Workflow.Fields.API_KEY, Workflow.Rest.createAPIUserForWorkflow(RoleName.WORKSPACE_ADMIN.getValue()));
    }

    private String getCompleteMessage(int numberItems) {
        return "Auto task has successfully added a comment to " + numberItems + " items.";
    }

    private void retryAutoTaskWithAPIUserAndCheck(WorkflowBase.Data autoTask, int numberItems) {
        UI.views.workflowView.workflowItemsContainer.grid.checkRow(autoTask);
        UI.views.workflowView.workflowItemsContainer.toolbar.clickRetryAutoTaskButton();
        UI.dialogs.messageDialogs.warning.clickButtonWithText("Retry");
        waitingPhase(autoTask, WorkflowAutoTaskPhase.COMPLETED.getValue());
        UI.views.workflowView.workflowItemsContainer.grid.getItemField(autoTask, MESSAGE).expectValueToBe(getCompleteMessage(numberItems));
    }

    private void prepareData() {
        workflow = Workflow.Rest.update(workflow, Workflow.Fields.API_KEY, null);
        reporter.info("Api key user is NOT defined in Workflow");
        bug1 = Bug.Rest.create(BugSeverity.LOW, BugPhase.NEW);
        Bug.Rest.update(bug1, Bug.Fields.DISTRIBUTION, distribution);
        bug2 = Bug.Rest.create(BugSeverity.MEDIUM, BugPhase.OPENED);
        Bug.Rest.update(bug2, Bug.Fields.DISTRIBUTION, distribution);
        bug3 = Bug.Rest.create(BugSeverity.HIGH, BugPhase.FIXED);
        Bug.Rest.update(bug3, Bug.Fields.DISTRIBUTION, release2);
        useCase = UseCase.Rest.create();
        UseCase.Rest.update(useCase, UseCase.Fields.DISTRIBUTION, distribution);
        qualitySpecification = QualitySpecification.Rest.create();
        QualitySpecification.Rest.update(qualitySpecification, QualitySpecification.Fields.DISTRIBUTION, distribution);
        functionality = Functionality.Rest.create();
        Functionality.Rest.update(functionality, Functionality.Fields.DISTRIBUTION, distribution);

        AddCommentTaskDTO dto1 = new AddCommentTaskDTO(UpdateProductEntityTypes.DEF,
                "\"(((distribution={id=" + distribution.getId() + "})))\"",
                "[\"{\\\"operator\\\":\\\"IN\\\",\\\"lExpression\\\":{\\\"operator\\\":\\\"PROPERTY\\\",\\\"value\\\":\\\"distribution\\\"},\\\"rExpression\\\":[{\\\"operator\\\":\\\"LITERAL\\\",\\\"value\\\":{\\\"id\\\":\\\"" + distribution.getId() + "\\\"}}]}\"]",
                comment);
        autoTask1 = WorkflowAutoTask.Rest.createAddCommentAction(stage, dto1);

        AddCommentTaskDTO dto2 = new AddCommentTaskDTO(UpdateProductEntityTypes.US,
                "\"(((distribution={id=" + distribution.getId() + "})))\"",
                "[\"{\\\"operator\\\":\\\"IN\\\",\\\"lExpression\\\":{\\\"operator\\\":\\\"PROPERTY\\\",\\\"value\\\":\\\"distribution\\\"},\\\"rExpression\\\":[{\\\"operator\\\":\\\"LITERAL\\\",\\\"value\\\":{\\\"id\\\":\\\"" + distribution.getId() + "\\\"}}]}\"]",
                comment);
        autoTask2 = WorkflowAutoTask.Rest.createAddCommentAction(stage, dto2);

        AddCommentTaskDTO dto3 = new AddCommentTaskDTO(UpdateProductEntityTypes.QS,
                "\"(((distribution={id=" + distribution.getId() + "})))\"",
                "[\"{\\\"operator\\\":\\\"IN\\\",\\\"lExpression\\\":{\\\"operator\\\":\\\"PROPERTY\\\",\\\"value\\\":\\\"distribution\\\"},\\\"rExpression\\\":[{\\\"operator\\\":\\\"LITERAL\\\",\\\"value\\\":{\\\"id\\\":\\\"" + distribution.getId() + "\\\"}}]}\"]",
                comment);
        autoTask3 = WorkflowAutoTask.Rest.createAddCommentAction(stage, dto3);

        AddCommentTaskDTO dto4 = new AddCommentTaskDTO(UpdateProductEntityTypes.FUNCTIONALITY,
                "\"(((distribution={id=" + distribution.getId() + "})))\"",
                "[\"{\\\"operator\\\":\\\"IN\\\",\\\"lExpression\\\":{\\\"operator\\\":\\\"PROPERTY\\\",\\\"value\\\":\\\"distribution\\\"},\\\"rExpression\\\":[{\\\"operator\\\":\\\"LITERAL\\\",\\\"value\\\":{\\\"id\\\":\\\"" + distribution.getId() + "\\\"}}]}\"]",
                comment);
        autoTask4 = WorkflowAutoTask.Rest.createAddCommentAction(stage, dto4);
    }

    private void prepareData2() {
        reporter.testStep("Create Workflow with autoTask to Add comment in US with filter that does not match existing US");
        setAPIUserToWorkflow();
        useCase = UseCase.Rest.create();
        UseCase.Rest.update(useCase, UseCase.Fields.DISTRIBUTION, distribution);
        AddCommentTaskDTO dto = new AddCommentTaskDTO(UpdateProductEntityTypes.US,
                "\"(((distribution={id=" + release2.getId() + "})))\"",
                "[\"{\\\"operator\\\":\\\"IN\\\",\\\"lExpression\\\":{\\\"operator\\\":\\\"PROPERTY\\\",\\\"value\\\":\\\"distribution\\\"},\\\"rExpression\\\":[{\\\"operator\\\":\\\"LITERAL\\\",\\\"value\\\":{\\\"id\\\":\\\"" + distribution.getId() + "\\\"}}]}\"]",
                comment);
        autoTask1 = WorkflowAutoTask.Rest.createAddCommentAction(stage, dto);
    }
}
