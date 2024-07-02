public class WorkflowAutoTaskRESTCallTest extends AutomationTest {
    public static final String REQRES_URL = "https://reqres.in/";
    public static final String RESTAPI_MOCK = "http://restapi-mock-workflow.test.net:8080/";
    private final static String PRODUCT_URL = "http://product-yes.test.net:8081/da/";
    private final static String PRODUCT_NO_CONNECT_URL = "http://product-no.test.net:8081/da";
    private final static String PLACEHOLDER_URL = "https://jsonplaceholder.typicode.com/";
    public static final String ALLOWED_URL_LIST = REQRES_URL + ";" + PRODUCT_URL + ";" + PRODUCT_NO_CONNECT_URL + ";" + PLACEHOLDER_URL + ";" + RESTAPI_MOCK;
    public static final String BODY_VALUE = "{\"firstName\": \"Kerry\", \"email\": \"test@google.com\"}";
    public static final String expressionType = "JSONPath";
    public static final String EXTRACTED_RESULT0 = "data0";
    public static final String EXTRACTED_RESULT1 = "result1";
    public static final String EXTRACTED_RESULT2 = "result2";
    private final static String OP_STATUS_CODE = "status_code";
    private final static String OP_EXTRACTED_RESULT = "extracted_result";
    private final static String OP_STATUS_CODE_EXPRESSION = "${this.output_parameters.status_code}";
    public static final String OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER1 = "${this.output_parameters.extracted_result['" + EXTRACTED_RESULT1 + "']}";
    public static final String OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER2 = "${this.output_parameters.extracted_result['" + EXTRACTED_RESULT2 + "']}";
    private final static String HEADER_KEY_1 = "header-key1";
    private final static String HEADER_VALUE_1 = "headerValue1";
    private final static String HEADER_KEY_2 = "header-key2";
    private final static String HEADER_VALUE_2 = "headerValue2";
    private final static String AUTH_HEADER_KEY = "Authorization";
    private final static String AUTH_HEADER_VALUE = "Basic YWRtaW46dkNAZX1gbiElS7RObkU="; //not real
    private final static String VAR_NAME_1 = "var1";
    private final static String VAR_NAME_2 = "var2";
    private final static String VAR_NAME_3 = "var3";
    private final static String VAR_NAME_4 = "var4";
    private final static String VAR_NAME_5 = "var5";
    protected Workflow.Data workflow;
    protected WorkflowStage.Data stage;

    @Before
    public void init() {
        workflow = Workflow.Rest.create();
        stage = WorkflowStage.Rest.createParallel(workflow);
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_ALLOW_HTTP.getValue(), true);
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_ALLOWED_URL_LIST.getValue(), ALLOWED_URL_LIST);
    }

    @Test
    public void addRestCallAutoTaskWithNoAuth() {
        String notURL = "https://google.com dfss   ggss";
        String notAllowedURL = "https://google.com.ua"; // valid URL that isn't on the list of allowed URLs
        navigateToEntity(stage);
        reporter.testStep("Add Auto Task - REST Call with 'No Auth' auth type");
        String name = "aa_RESTCall_" + RandomStringUtils.randomAlphabetic(5);
        UI.views.workflowStageView.stageItemsContainer.toolbar.openToolbarAddAutoTaskDialog();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setField(WorkflowAutoTask.Fields.NAME, name);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setActionType(WorkflowAutoTaskType.REST_CALL_TASK_TYPE);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectAuthTypeValue(WorkflowAutoTaskAuthType.TYPE_NO_AUTH);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setHTTPMethod(WorkflowAutoTaskHTTPMethod.GET);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectExpressionTypeValue(expressionType);
        reporter.testStep("Add and check 'URL' field");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.getUrlExpressionIcon().expectVisible();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setURL(notURL);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        checkErrorMessageAndClose(WorkflowAutoTask.ErrorMessage.URL_IS_INVALID.getValue());
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setURL(notAllowedURL);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        checkErrorMessageAndClose(WorkflowAutoTask.ErrorMessage.URL_IS_NOT_ON_THE_ALLOWED_LIST.getValue());
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setURL(REQRES_URL);
        reporter.testStep("Add 'Header' fields and check their validation.");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.clickAddHeader();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.checkAddHeaderButtonState(false);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.getHeaderValueExpressionIcon(0).expectVisible();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setHeaderKey(0, HEADER_KEY_1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectHeaderValueIsRequired(0);
        reporter.info("AutoTask is not created. The header values are required.");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setHeaderValue(0, HEADER_VALUE_1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.checkAddHeaderButtonState(true);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.clickAddHeader();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectHeaderKeyIsRequired(1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setHeaderKey(1, HEADER_KEY_1); // Add 2nd Header with the same name (key)
        reporter.info("Header keys are no longer unique. Users can specify a few headers with the same name.");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setHeaderValue(1, HEADER_VALUE_2);
        reporter.testStep("Provide 'Body' field");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.getBodyExpressionIcon().expectVisible();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setBody(BODY_VALUE);
        reporter.testStep("Add and check 'Expressions to parse response' field");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.checkTooltipTextForExpressionFieldInfoIcon();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.clickAddExpression();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.checkAddExpressionButtonState(false);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectExpressionKeyField(0, EXTRACTED_RESULT0); // The "data0" placeholder is displayed in the expression key field
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setExpressionKey(0, EXTRACTED_RESULT1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectExpressionValueIsRequired(0);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setExpressionValue(0, "$.id."); // set incorrect expression value
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        checkErrorMessageAndClose(WorkflowAutoTask.ErrorMessage.INVALID_JSONPATH_EXPRESSION.getValue());
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setExpressionValue(0, "$.id");
        reporter.testStep("Add 2nd expression with the same key and check that key field value should be unique");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.clickAddExpression();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setExpressionKey(1, EXTRACTED_RESULT1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setExpressionValue(1, "$");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectExpressionsKeyFieldValueIsUnique(1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setExpressionKey(1, EXTRACTED_RESULT2);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        GeneralUtils.delay(3000);
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.getEntityWithFields(name);
        assertEquals("Wrong phase AutoTask: ", WorkflowAutoTaskPhase.PLANNED.getValue(), autoTask.getPhaseName());
        assertEquals("AutoTask has the wrong action type: ", WorkflowAutoTaskType.REST_CALL_TASK_TYPE.getValue(), autoTask.getTaskType());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        reporter.testStep("Verify that all set field values are correctly displayed on the AutoTask Details tab");
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, TASK_TYPE, WorkflowAutoTaskType.REST_CALL_TASK_TYPE.getLabel());
        UI.views.workflowAutoTaskView.restCallView.expectAuthTypeValue(WorkflowAutoTaskAuthType.TYPE_NO_AUTH);
        UI.views.workflowAutoTaskView.restCallView.expectHTTPMethodValue(WorkflowAutoTaskHTTPMethod.GET);
        UI.views.workflowAutoTaskView.restCallView.expectUrlValue(REQRES_URL);
        UI.views.workflowAutoTaskView.restCallView.expectExpressionTypeValue(expressionType);
        UI.views.workflowAutoTaskView.restCallView.expectHeaderKeyAndValue(0, HEADER_KEY_1, HEADER_VALUE_1);
        UI.views.workflowAutoTaskView.restCallView.expectHeaderKeyAndValue(1, HEADER_KEY_1, HEADER_VALUE_2);
        UI.views.workflowAutoTaskView.restCallView.expectBodyValue(BODY_VALUE);
        UI.views.workflowAutoTaskView.restCallView.expectExpressionKeyAndValue(0, EXTRACTED_RESULT1, "$.id");
        UI.views.workflowAutoTaskView.restCallView.expectExpressionKeyAndValue(1, EXTRACTED_RESULT2, "$");
    }

    @Test
    public void addRestCallAutoTaskWithAPIKey() {
        WorkflowVariableString.Rest.create(VAR_NAME_1, "page", "page", workflow);
        WorkflowVariableString.Rest.create(VAR_NAME_2, "header", "header", workflow);
        WorkflowVariableString.Rest.create(VAR_NAME_3, null, null, workflow);
        WorkflowVariableString.Rest.create(VAR_NAME_4, null, null, workflow);
        WorkflowVariableString.Rest.create(VAR_NAME_5, BODY_VALUE, BODY_VALUE, workflow);
        navigateToEntity(stage);
        reporter.testStep("Add Auto Task - REST Call with 'API Key' auth type");
        String name = "aa_RESTCall_" + RandomStringUtils.randomAlphabetic(5);
        UI.views.workflowStageView.stageItemsContainer.toolbar.openToolbarAddAutoTaskDialog();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setField(WorkflowAutoTask.Fields.NAME, name);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setActionType(WorkflowAutoTaskType.REST_CALL_TASK_TYPE);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setAuthType(WorkflowAutoTaskAuthType.TYPE_API_KEY);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.checkAddAuthHeaderButtonState(true);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.clickAddAuthHeader();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.checkAddAuthHeaderButtonState(false);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setAuthHeaderKey(0, HEADER_KEY_1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setAuthHeaderValue(0, HEADER_VALUE_2);
        reporter.testStep("Check that Auth Header field values should be encrypted and not shown to users");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectAuthHeaderValueFieldHasPasswordType(0);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.clickAddAuthHeader();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setAuthHeaderKey(1, HEADER_KEY_1);
        reporter.testStep("For fields with the same auth header name, an icon with the tooltip 'The value should be unique' should be displayed");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectAuthHeaderKeyFieldValueIsUnique(1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.deleteAuthHeaderRow(1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setHTTPMethod(WorkflowAutoTaskHTTPMethod.PUT);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setURL(REQRES_URL);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAddAndEdit();
        reporter.testStep("AutoTask should be created. Verify that all set field values are correctly displayed on the AutoTask Details tab");
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, TASK_TYPE, WorkflowAutoTaskType.REST_CALL_TASK_TYPE.getLabel());
        UI.views.workflowAutoTaskView.restCallView.expectAuthTypeValue(WorkflowAutoTaskAuthType.TYPE_API_KEY);
        UI.views.workflowAutoTaskView.restCallView.expectHTTPMethodValue(WorkflowAutoTaskHTTPMethod.PUT);
        UI.views.workflowAutoTaskView.restCallView.expectUrlValue(REQRES_URL);
        UI.views.workflowAutoTaskView.restCallView.expectExpressionTypeValue(expressionType);
        UI.views.workflowAutoTaskView.restCallView.expectAuthHeaderKeyField(0, HEADER_KEY_1);
        UI.views.workflowAutoTaskView.restCallView.expectAuthHeaderValueFieldHasPasswordType(0);
        // Update AutoTask
        reporter.testStep("Change 'URL' field value (add some prefix at the end of URL)");
        String changedUrl = REQRES_URL + "api/";
        UI.views.workflowAutoTaskView.restCallView.setURL(changedUrl);
        UI.views.workflowAutoTaskView.restCallView.expectAuthHeaderValueFieldIsCleared(0);
        UI.views.workflowAutoTaskView.restCallView.clickSave();
        UI.dialogs.messageDialogs.info.expectVisible();
        UI.dialogs.messageDialogs.info.expectMessageDialogToContain("Some of your fields have values that are not valid. See the error icon next to each field and try saving again.");
        UI.dialogs.messageDialogs.info.clickClose();
        reporter.testStep("Discard changes -> Parameterize the 'URL' field using variables");
        UI.views.workflowAutoTaskView.restCallView.clickRestore();
        UI.views.workflowAutoTaskView.restCallView.parameterizeUrlUsingInsertVariableIcon(VAR_NAME_1);
        UI.views.workflowAutoTaskView.restCallView.expectAuthHeaderValueFieldIsCleared(0);
        reporter.testStep("Check that when the auth header key is changed, its value is cleared");
        UI.views.workflowAutoTaskView.restCallView.setAuthHeaderValue(0, HEADER_VALUE_1);
        UI.views.workflowAutoTaskView.restCallView.setAuthHeaderKey(0, HEADER_KEY_2);
        UI.views.workflowAutoTaskView.restCallView.expectAuthHeaderValueFieldIsCleared(0);
        UI.views.workflowAutoTaskView.restCallView.setAuthHeaderValue(0, HEADER_VALUE_1);
        reporter.testStep("Add header with parametrized value");
        UI.views.workflowAutoTaskView.restCallView.clickAddHeader();
        UI.views.workflowAutoTaskView.restCallView.setHeaderKey(0, HEADER_KEY_1);
        UI.views.workflowAutoTaskView.restCallView.setHeaderValue(0, "${" + VAR_NAME_2 + "}");
        reporter.testStep("Provide body with parametrization");
        UI.views.workflowAutoTaskView.restCallView.parameterizeBodyUsingInsertVariableIcon(VAR_NAME_5);
        reporter.testStep("Check that the expression field does not support parameterization");
        UI.views.workflowAutoTaskView.restCallView.clickAddExpression();
        UI.views.workflowAutoTaskView.restCallView.expectExpressionKeyField(0, EXTRACTED_RESULT0);
        UI.views.workflowAutoTaskView.restCallView.setExpressionValue(0, "${" + VAR_NAME_1 + "}");
        UI.views.workflowAutoTaskView.restCallView.clickSave();
        checkErrorMessageAndClose(WorkflowAutoTask.ErrorMessage.INVALID_JSONPATH_EXPRESSION.getValue());
        UI.views.workflowAutoTaskView.restCallView.setExpressionKey(0, EXTRACTED_RESULT1);
        UI.views.workflowAutoTaskView.restCallView.setExpressionValue(0, "$.id");
        UI.views.workflowAutoTaskView.restCallView.clickSave();
        // Advanced result handling
        String opWrongStatusCodeExpression = "${this.output_parameters.status_cccode}";
        String opExtractedResultExpression = "${this.output_parameters.extracted_result['']}";
        UI.views.workflowAutoTaskView.restCallView.selectAndParameterizeVariableUsingInsertVariableIcon(VAR_NAME_3, OP_STATUS_CODE);
        UI.views.workflowAutoTaskView.restCallView.clickSave();
        reporter.testStep("Check the validation of variable values in result handling");
        UI.views.workflowAutoTaskView.restCallView.setVariableValue(VAR_NAME_3, opWrongStatusCodeExpression);
        UI.views.workflowAutoTaskView.restCallView.clickSave();
        UI.dialogs.messageDialogs.error.expectVisible();
        UI.dialogs.messageDialogs.error.expectMessageDialogToContain("The expression '" + opWrongStatusCodeExpression + "' contains an unknown field");
        UI.dialogs.messageDialogs.error.clickClose();
        UI.views.workflowAutoTaskView.restCallView.clickRestore();
        UI.views.workflowAutoTaskView.restCallView.clickAddLineVariable();
        UI.views.workflowAutoTaskView.restCallView.selectAndParameterizeVariableUsingInsertVariableIcon(VAR_NAME_4, OP_EXTRACTED_RESULT);
        UI.views.workflowAutoTaskView.restCallView.clickSave();
        UI.dialogs.messageDialogs.error.expectVisible();
        UI.dialogs.messageDialogs.error.expectMessageDialogToContain("The expression '" + opExtractedResultExpression + "' contains an unknown field");
        UI.dialogs.messageDialogs.error.clickClose();
        UI.views.workflowAutoTaskView.restCallView.setVariableValue(VAR_NAME_4, OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER1);
        reporter.info("var3 = status code, var4 = parsed response");
        UI.views.workflowAutoTaskView.restCallView.clickSave();
        GeneralUtils.delay(3000);
        WorkflowAutoTask.Data autoTask1 = WorkflowAutoTask.Rest.getEntityWithFields(name);
        // Duplicate AutoTask
        navigateToEntity(workflow);
        UI.views.workflowView.workflowItemsContainer.grid.selectRow(autoTask1);
        reporter.testStep("Duplicate AutoTask and check field values");
        String newAaId = UI.views.workflowView.workflowItemsContainer.toolbar.duplicateAndGetId();
        WorkflowAutoTask.Data autoTask2 = WorkflowAutoTask.Rest.getEntityByIdWithFields(newAaId);
        navigateToEntity(autoTask2);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, TASK_TYPE, WorkflowAutoTaskType.REST_CALL_TASK_TYPE.getLabel());
        UI.views.workflowAutoTaskView.restCallView.expectAuthTypeValue(WorkflowAutoTaskAuthType.TYPE_API_KEY);
        UI.views.workflowAutoTaskView.restCallView.expectHTTPMethodValue(WorkflowAutoTaskHTTPMethod.PUT);
        String expectedUrl = REQRES_URL + "${" + VAR_NAME_1 + "}";
        UI.views.workflowAutoTaskView.restCallView.expectUrlValue(expectedUrl);
        UI.views.workflowAutoTaskView.restCallView.expectExpressionTypeValue(expressionType);
        UI.views.workflowAutoTaskView.restCallView.expectAuthHeaderKeyField(0, HEADER_KEY_2);
        UI.views.workflowAutoTaskView.restCallView.expectAuthHeaderValueFieldHasPasswordType(0);
        UI.views.workflowAutoTaskView.restCallView.expectHeaderKeyAndValue(0, HEADER_KEY_1, "${" + VAR_NAME_2 + "}");
        UI.views.workflowAutoTaskView.restCallView.expectBodyValue("${" + VAR_NAME_5 + "}");
        UI.views.workflowAutoTaskView.restCallView.expectExpressionKeyAndValue(0, EXTRACTED_RESULT1, "$.id");
        UI.views.workflowAutoTaskView.restCallView.expectVariableValue(VAR_NAME_3, OP_STATUS_CODE_EXPRESSION);
        UI.views.workflowAutoTaskView.restCallView.expectVariableValue(VAR_NAME_4, OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER1);
        // Delete AutoTask
        navigateToEntity(workflow);
        UI.views.workflowView.workflowItemsContainer.grid.selectRow(autoTask2);
        UI.views.workflowView.workflowItemsContainer.toolbar.clickDelete();
        UI.dialogs.warningMessageDialog.clickDelete();
        UI.views.workflowView.workflowItemsContainer.grid.expectNotVisible(autoTask2);
    }

    @Test
    public void addAndAnotherForRestCallAutoTask() {
        navigateToEntity(stage);
        String name1 = "aa1_RESTCall_" + RandomStringUtils.randomAlphabetic(5);
        UI.views.workflowStageView.stageItemsContainer.toolbar.openToolbarAddAutoTaskDialog();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setField(WorkflowAutoTask.Fields.NAME, name1);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setActionType(WorkflowAutoTaskType.REST_CALL_TASK_TYPE);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectAuthTypeValue(WorkflowAutoTaskAuthType.TYPE_NO_AUTH);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setHTTPMethod(WorkflowAutoTaskHTTPMethod.DELETE);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setURL(REQRES_URL);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAddAnother();
        String name2 = "aa2_RESTCall_" + RandomStringUtils.randomAlphabetic(5);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, TASK_TYPE, WorkflowAutoTaskType.REST_CALL_TASK_TYPE.getLabel());
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setField(WorkflowAutoTask.Fields.NAME, name2);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setAuthType(WorkflowAutoTaskAuthType.TYPE_API_KEY);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setHTTPMethod(WorkflowAutoTaskHTTPMethod.POST);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setURL(REQRES_URL);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        reporter.testStep("Check that 2 AutoTask with REST Call action type have been successfully added");
        GeneralUtils.delay(3000);
        WorkflowAutoTask.Data autoTask1 = WorkflowAutoTask.Rest.getEntityWithFields(name1);
        WorkflowAutoTask.Data autoTask2 = WorkflowAutoTask.Rest.getEntityWithFields(name2);
        assertEquals("Wrong phase AutoTask: ", WorkflowAutoTaskPhase.PLANNED.getValue(), autoTask1.getPhaseName());
        assertEquals("AutoTask has the wrong action type: ", WorkflowAutoTaskType.REST_CALL_TASK_TYPE.getValue(), autoTask1.getTaskType());
        assertEquals("Wrong phase AutoTask: ", WorkflowAutoTaskPhase.PLANNED.getValue(), autoTask2.getPhaseName());
        assertEquals("AutoTask has the wrong action type: ", WorkflowAutoTaskType.REST_CALL_TASK_TYPE.getValue(), autoTask2.getTaskType());
    }

    @Test
    public void checkParameterAllowHttpForRestCallAutoTask() {
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_ALLOW_HTTP.getValue(), false);
        navigateToEntity(stage);
        String name = "aa_RESTCall_" + RandomStringUtils.randomAlphabetic(5);
        UI.views.workflowStageView.stageItemsContainer.toolbar.openToolbarAddAutoTaskDialog();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setField(WorkflowAutoTask.Fields.NAME, name);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setActionType(WorkflowAutoTaskType.REST_CALL_TASK_TYPE);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setAuthType(WorkflowAutoTaskAuthType.TYPE_API_KEY);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.clickAddAuthHeader();
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setAuthHeaderKey(0, AUTH_HEADER_KEY);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setAuthHeaderValue(0, AUTH_HEADER_VALUE);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setHTTPMethod(WorkflowAutoTaskHTTPMethod.GET);
        reporter.testStep("Check that AutoTask will not be created with a URL with the HTTP protocol when the parameter prohibits the use of HTTP.");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.restCallView.setURL(PRODUCT_URL + "rest/deploy/application/");
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        checkErrorMessageAndClose(WorkflowAutoTask.ErrorMessage.PROTOCOL_IS_NOT_SUPPORTED.getValue());
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_ALLOW_HTTP.getValue(), true);
        UI.dialogs.workflowDialogs.addAutoTaskDialog.clickAdd();
        GeneralUtils.delay(3000);
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.getEntityWithFields(name);
        assertEquals("AutoTask has the wrong action type: ", WorkflowAutoTaskType.REST_CALL_TASK_TYPE.getValue(), autoTask.getTaskType());
        reporter.testStep("Using the parameter, disable the use of HTTP and try to execute AutoTask with a URL with the HTTP protocol.");
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_ALLOW_HTTP.getValue(), false);
        Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_PROTOCOL_IS_NOT_SUPPORTED.getValue());
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_ALLOW_HTTP.getValue(), true);
        GeneralUtils.delay(3000);
        UI.views.workflowAutoTaskView.restCallView.clickRetryAutoTask();
        UI.dialogs.warningMessageDialog.clickOK();
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.COMPLETED.getValue());
        UI.views.workflowAutoTaskView.restCallView.clickRefresh();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_SUCCESSFUL.getValue());
    }

    @Test
    public void executeRestCallAutoTaskWithNoAuthAndParametrization() {
        String VAR_NAME_4 = "reg";
        String VAR_NAME_5 = "firstName";
        String BODY_VALUE = "{\"firstName\": \"${" + VAR_NAME_5 + "}\", \"email\": \"test@google.com\"}";
        String OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER0 = "${this.output_parameters.extracted_result['" + EXTRACTED_RESULT0 + "']}";
        WorkflowVariableString.Rest.create(VAR_NAME_1, null, null, workflow);
        WorkflowVariableString.Rest.create(VAR_NAME_2, null, null, workflow);
        WorkflowVariableString.Rest.create(VAR_NAME_3, null, null, workflow);
        WorkflowVariableString.Rest.create(VAR_NAME_4, "users", "users", workflow);
        WorkflowVariableString.Rest.create(VAR_NAME_5, "Kerry", "Kerry", workflow);

        reporter.testStep("Create REST API Call AutoTask with 'No Auth' auth type using wrong Expression value");
        RestAPICallDTO.Expression result0Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT0, "body");
        RestAPICallDTO.Expression result1Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT1, "firstName");
        RestAPICallDTO.Expression result2Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT2, "email");
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(RESTAPI_MOCK + "${" + VAR_NAME_4 + "}"),
                WorkflowAutoTaskHTTPMethod.POST.getValue(), null, createExpression(BODY_VALUE), DEFAULT_EXPRESSION_TYPE,
                List.of(result0Expression, result1Expression, result2Expression), RestAPICallDTO.Authentication.createNoAuthInstance());
        ResultHandlingDTO resultHandlingDTO = new ResultHandlingDTO();
        resultHandlingDTO.setVariableAssignments(List.of(
                new ResultHandlingDTO.VariableAssignment(VAR_NAME_1, OP_STATUS_CODE_EXPRESSION, true),
                new ResultHandlingDTO.VariableAssignment(VAR_NAME_2, OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER0, true),
                new ResultHandlingDTO.VariableAssignment(VAR_NAME_3, OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER2, true)));
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO, resultHandlingDTO);

        reporter.testStep("Execute AutoTask and check execution message");
        Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, "REST call finished, but the specified expression [" + EXTRACTED_RESULT0 + "] could not parse a value from the response.");
        UI.views.workflowAutoTaskView.restCallView.expectStatusCodeValue(201);
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(0, EXTRACTED_RESULT1, "Kerry");
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(1, EXTRACTED_RESULT2, "test@google.com");
        UI.views.workflowAutoTaskView.restCallView.deleteExpressionRow(0);
        saveAndRetryAutoTask();
        GeneralUtils.delay(3000);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue());
        UI.views.workflowAutoTaskView.restCallView.clickRefresh();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.OUTPUT_PROCESSING_CANNOT_PERFORM.getValue() + "'" + VAR_NAME_2 + "'");
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, "invalid in the expression '" + OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER0 + "'");
        UI.views.workflowAutoTaskView.restCallView.setVariableValue(VAR_NAME_2, OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER1);
        saveAndRetryAutoTask();
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.COMPLETED.getValue());
        UI.views.workflowAutoTaskView.restCallView.clickRefresh();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.OUTPUT_PROCESSING_SUCCESS.getValue());
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, EXECUTION_URL, RESTAPI_MOCK + "users");
        reporter.testStep("Verify that values for variables: var1, var2, var3 contain correct values after AutoTask execution");
        assertThat(WorkflowVariableString.Rest.getVariableByName(VAR_NAME_1, Long.valueOf(workflow.getId())).getValue(), is("201"));
        assertThat(WorkflowVariableString.Rest.getVariableByName(VAR_NAME_2, Long.valueOf(workflow.getId())).getValue(), is("Kerry"));
        assertThat(WorkflowVariableString.Rest.getVariableByName(VAR_NAME_3, Long.valueOf(workflow.getId())).getValue(), is("test@google.com"));
    }

    @Test
    public void executeRestCallAutoTaskWithAPIKeyAuthType() {
        String authHeaderWrongValue = "Basic gm9obkBliLFtsGxlLmKybTphYmMoMjM="; //not real
        String url = PRODUCT_URL + "rest/deploy/application/2e5f0123-2d75-43c2-9124-9773322abcddc/snapshotsPaged"; //not real
        String parsedResponseValue1 = "[\"Name_snapshot\",\"blanck_snapshot\"]";
        String parsedResponseValue2 = "12b3ffbc-c456-789b-012b-34b56bb7bcf8"; //not real
        WorkflowVariableString.Rest.create(VAR_NAME_1, null, null, workflow);
        WorkflowVariableString.Rest.create(VAR_NAME_2, null, null, workflow);

        reporter.testStep("Create REST API Call AutoTask with 'API Key' auth type using wrong Auth Header value");
        RestAPICallDTO.Expression result1Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT1, "$.records[:2].name");
        RestAPICallDTO.Expression result2Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT2, "$.records[1].id");
        RestAPICallDTO.Authentication authentication = new RestAPICallDTO.Authentication();
        authentication.authType = RestAPICallDTO.Authentication.TYPE_API_KEY;
        authentication.authHeaders = new ArrayList<>() {{
            add(new RestAPICallDTO.AuthHeaders(AUTH_HEADER_KEY, authHeaderWrongValue));
        }};
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(url), WorkflowAutoTaskHTTPMethod.GET.getValue(),
                null, null, DEFAULT_EXPRESSION_TYPE, List.of(result1Expression, result2Expression), authentication);
        ResultHandlingDTO resultHandlingDTO = new ResultHandlingDTO();
        resultHandlingDTO.setVariableAssignments(List.of(
                new ResultHandlingDTO.VariableAssignment(VAR_NAME_1, OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER1, true),
                new ResultHandlingDTO.VariableAssignment(VAR_NAME_2, OP_EXTRACTED_RESULT_EXPRESSION_WITH_ER2, true)));
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO, resultHandlingDTO);

        reporter.testStep("Execute AutoTask ->  Verify that AutoTask should fail with status code = 401");
        Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_FAILED.getValue());
        UI.views.workflowAutoTaskView.restCallView.expectStatusCodeValue(401);
        reporter.testStep("Set correct Auth Header value -> Retry AutoTask -> Verify that AutoTask should be completed with status code = 200");
        UI.views.workflowAutoTaskView.restCallView.setAuthHeaderValue(0, AUTH_HEADER_VALUE);
        saveAndRetryAutoTask();
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.COMPLETED.getValue());
        UI.views.workflowAutoTaskView.restCallView.clickRefresh();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.OUTPUT_PROCESSING_SUCCESS.getValue());
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, EXECUTION_URL, url);
        UI.views.workflowAutoTaskView.restCallView.expectStatusCodeValue(200);
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(0, EXTRACTED_RESULT1, parsedResponseValue1);
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(1, EXTRACTED_RESULT2, parsedResponseValue2);
        reporter.testStep("Verify workflow variable values after AutoTask execution");
        assertThat(WorkflowVariableString.Rest.getVariableByName(VAR_NAME_1, Long.valueOf(workflow.getId())).getValue(), is(parsedResponseValue1));
        assertThat(WorkflowVariableString.Rest.getVariableByName(VAR_NAME_2, Long.valueOf(workflow.getId())).getValue(), is(parsedResponseValue2));
    }

    @Test
    public void executeRestCallAutoTaskWithDeleteMethod() {
        String url = PLACEHOLDER_URL + "posts/1";
        RestAPICallDTO.Expression result1Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT1, "$");
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(url), WorkflowAutoTaskHTTPMethod.DELETE.getValue(),
                null, null, DEFAULT_EXPRESSION_TYPE, List.of(result1Expression), RestAPICallDTO.Authentication.createNoAuthInstance());
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO);
        workflow = Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.COMPLETED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_SUCCESSFUL.getValue());
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, EXECUTION_URL, url);
        UI.views.workflowAutoTaskView.restCallView.expectStatusCodeValue(200);
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(0, EXTRACTED_RESULT1, "{}");
    }

    @Test
    public void executeRestCallAutoTaskWithPutMethod() {
        String url = RESTAPI_MOCK + "users/5";
        RestAPICallDTO.Expression idExpression = new RestAPICallDTO.Expression(EXTRACTED_RESULT0, "id");
        RestAPICallDTO.Expression nameExpression = new RestAPICallDTO.Expression(EXTRACTED_RESULT1, "firstName");
        RestAPICallDTO.Expression bodyExpression = new RestAPICallDTO.Expression(EXTRACTED_RESULT2, "email");
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(url), WorkflowAutoTaskHTTPMethod.PUT.getValue(),
                null, createExpression(BODY_VALUE), DEFAULT_EXPRESSION_TYPE,
                List.of(idExpression, nameExpression, bodyExpression), RestAPICallDTO.Authentication.createNoAuthInstance());
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO);
        workflow = Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.COMPLETED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_SUCCESSFUL.getValue());
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, EXECUTION_URL, url);
        UI.views.workflowAutoTaskView.restCallView.expectStatusCodeValue(200);
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(0, EXTRACTED_RESULT0, "5");
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(1, EXTRACTED_RESULT1, "Kerry");
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(2, EXTRACTED_RESULT2, "test@google.com");
    }

    @Test
    public void verifyMessageRequestFailed() {
        String url = PRODUCT_NO_CONNECT_URL + "rest/deploy/application/";
        reporter.testStep("Create REST API Call AutoTask with 'URL' that is not accessible from your instance");
        RestAPICallDTO.Authentication authentication = new RestAPICallDTO.Authentication();
        authentication.authType = RestAPICallDTO.Authentication.TYPE_API_KEY;
        authentication.authHeaders = new ArrayList<>() {{
            add(new RestAPICallDTO.AuthHeaders(AUTH_HEADER_KEY, AUTH_HEADER_VALUE));
        }};
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(url), WorkflowAutoTaskHTTPMethod.GET.getValue(),
                null, null, DEFAULT_EXPRESSION_TYPE, null, authentication);
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO);
        reporter.testStep("Execute AutoTask ->  Verify that AutoTask should fail with corresponding execution message");
        Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue(), 120000);
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, EXECUTION_URL, url);
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_NO_CONNECTIVITY.getValue());
    }

    @Test
    public void verifyMessageIfIllegalHeaderValue() {
        String url = REQRES_URL + "api/users/2";
        reporter.testStep("Create REST API Call AutoTask with 'Header' that contain illegal value");
        RestAPICallDTO.Header header1 = new RestAPICallDTO.Header(HEADER_KEY_1, createExpression("кирилиця"));
        RestAPICallDTO.Header header2 = new RestAPICallDTO.Header(HEADER_KEY_2, createExpression("a b"));
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(url), WorkflowAutoTaskHTTPMethod.GET.getValue(),
                List.of(header1, header2), null, DEFAULT_EXPRESSION_TYPE, null, RestAPICallDTO.Authentication.createNoAuthInstance());
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO);
        reporter.testStep("Execute AutoTask ->  Verify that AutoTask should fail with corresponding execution message");
        Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, "Header [" + HEADER_KEY_1 + "] " + WorkflowAutoTask.Message.REST_CALL_ILLEGAL_HEADER_VALUE.getValue());
        UI.views.workflowAutoTaskView.restCallView.deleteHeaderRow(0);
        saveAndRetryAutoTask();
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue());
        UI.views.workflowAutoTaskView.restCallView.clickRefresh();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, "Header [" + HEADER_KEY_2 + "] " + WorkflowAutoTask.Message.REST_CALL_ILLEGAL_HEADER_VALUE.getValue());
    }

    @Test
    public void verifyRequestTimeoutParameter() {
        String url = RESTAPI_MOCK + "tech/delay?seconds=20";
        String newUrl = RESTAPI_MOCK + "tech/delay?seconds=3";
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_REQUEST_TIMEOUT.getValue(), "10");
        reporter.testStep("Create REST API Call AutoTask with URL that gives more delay to response than waits of the request timeout parameter.");
        RestAPICallDTO.Expression result1Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT1, "$");
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(url), WorkflowAutoTaskHTTPMethod.GET.getValue(), null, null,
                DEFAULT_EXPRESSION_TYPE, List.of(result1Expression), RestAPICallDTO.Authentication.createNoAuthInstance());
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO);
        reporter.testStep("Execute AutoTask ->  Verify that AutoTask should fail with 'no connectivity' execution message");
        Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_NO_CONNECTIVITY.getValue());
        UI.views.workflowAutoTaskView.restCallView.setURL(newUrl);
        saveAndRetryAutoTask();
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.COMPLETED.getValue());
        UI.views.workflowAutoTaskView.restCallView.clickRefresh();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, EXECUTION_URL, newUrl);
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_SUCCESSFUL.getValue());
        UI.views.workflowAutoTaskView.restCallView.expectStatusCodeValue(200);
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(0, EXTRACTED_RESULT1, "delayed");
        SettingsUtils.resetSpaceParameters(ContextApi.getContext().getSpaceId(), Param.WF_AT_REST_CALL_REQUEST_TIMEOUT.getValue());
    }

    @Test
    public void verifyMaxRequestResponseSizeParameter() {
        String url = RESTAPI_MOCK + "tech/large-response";
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_MAX_REQUEST_RESPONSE_SIZE.getValue(), "1");
        reporter.testStep("Create REST API Call AutoTask with URL whose response will exceed the 1 MB limit");
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(url), WorkflowAutoTaskHTTPMethod.GET.getValue(), null, null,
                DEFAULT_EXPRESSION_TYPE, null, RestAPICallDTO.Authentication.createNoAuthInstance());
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO);
        reporter.testStep("Execute AutoTask ->  Verify that AutoTask should fail with 'response size exceeded' execution message");
        Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_RESPONSE_SIZE_EXCEEDED.getValue());
    }

    @Test
    public void parameterForTrialUserIsNotApplicableInNonTrialLicenseForRestCallAutoTask() {
        String url = REQRES_URL + "api/users?page=2";
        reporter.testStep("Set the parameter with the list of allowed URLs to the correct URL that will be used in REST Call AutoTask.");
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_TEST_ALLOWED_URL_LIST.getValue(), REQRES_URL);
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(url), WorkflowAutoTaskHTTPMethod.GET.getValue(), null, null,
                DEFAULT_EXPRESSION_TYPE, null, RestAPICallDTO.Authentication.createNoAuthInstance());
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO);
        reporter.testStep("Remove 'https://reqres.in/' from parameter with allowed URL list");
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_ALLOWED_URL_LIST.getValue(), "");
        reporter.testStep("Execute AutoTask ->  Verify that AutoTask should fail with corresponding execution message");
        Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.FAILED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_URL_IS_NOT_ALLOWED.getValue());
        SettingsUtils.setSpaceParameters(Param.WF_AT_REST_CALL_ALLOWED_URL_LIST.getValue(), ALLOWED_URL_LIST);
    }

    @Test
    public void checkReturnRequestHeadersForRestCallAutoTask() {
        String url = RESTAPI_MOCK + "tech/headers";
        String host = RESTAPI_MOCK.substring(7, RESTAPI_MOCK.length() - 1);
        reporter.testStep("Create REST API Call AutoTask with specified headers");
        RestAPICallDTO.Header header1 = new RestAPICallDTO.Header(HEADER_KEY_1, createExpression(HEADER_VALUE_1));
        RestAPICallDTO.Expression header0Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT0, "content-type");
        RestAPICallDTO.Expression header1Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT1, HEADER_KEY_1);
        RestAPICallDTO.Expression header2Expression = new RestAPICallDTO.Expression(EXTRACTED_RESULT2, "host");
        RestAPICallDTO restCallDTO = new RestAPICallDTO(createExpression(url), WorkflowAutoTaskHTTPMethod.GET.getValue(), List.of(header1), null,
                DEFAULT_EXPRESSION_TYPE, List.of(header0Expression, header1Expression, header2Expression), RestAPICallDTO.Authentication.createNoAuthInstance());
        WorkflowAutoTask.Data autoTask = WorkflowAutoTask.Rest.createRestCallAutoTask(stage, restCallDTO);
        reporter.testStep("Execute AutoTask ->  Verify that AutoTask receives header values");
        Workflow.Rest.startWorkflow(workflow);
        WorkflowUtils.waitingPhase(autoTask, WorkflowAutoTaskPhase.COMPLETED.getValue());
        navigateToEntity(autoTask);
        UI.views.workflowAutoTaskView.clickDetailsTab();
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, MESSAGE, WorkflowAutoTask.Message.REST_CALL_SUCCESSFUL.getValue());
        UI.views.workflowAutoTaskView.restCallView.expectFieldText(WorkflowAutoTask.ENTITY_TYPE, EXECUTION_URL, url);
        UI.views.workflowAutoTaskView.restCallView.expectStatusCodeValue(200);
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(0, EXTRACTED_RESULT0, "application/json");
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(1, EXTRACTED_RESULT1, HEADER_VALUE_1);
        UI.views.workflowAutoTaskView.restCallView.expectExtractedResult(2, EXTRACTED_RESULT2, host);
    }

    private void checkErrorMessageAndClose(String errorMessage) {
        reporter.testStep("Check that an error message has appeared with the appropriate text");
        UI.dialogs.messageDialogs.error.expectVisible();
        UI.dialogs.messageDialogs.error.expectMessageDialogToContain(errorMessage);
        UI.dialogs.messageDialogs.error.clickClose();
    }

    private void saveAndRetryAutoTask() {
        UI.views.workflowAutoTaskView.restCallView.clickSave();
        GeneralUtils.delay(3000);
        UI.views.workflowAutoTaskView.restCallView.clickRetryAutoTask();
        UI.dialogs.warningMessageDialog.clickOK();
    }
}
