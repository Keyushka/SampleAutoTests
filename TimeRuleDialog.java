public class TimeRuleDialog extends Dialog {

    private final String editorDisplayValueDataAid = "field-editor-display-value";
    private final BaseElement startImmediatelyField = new BaseElement(Locator.dataAid("start-immediately-field"), this);
    private final Button addTimeIntervalButton = new Button(Locator.dataAid("add-time-interval-button"), this);
    private final BaseElement referencePointField = new BaseElement(Locator.dataAid("reference-point-field"), this);

    private final BaseElement unitSelector = new BaseElement(Locator.dataAid("time-unit-selector"), this);
    private final BaseElement operatorSelector = new BaseElement(Locator.dataAid("time-operator-selector"), this);
    private final BaseElement offsetInputField = new BaseElement(Locator.dataAid("time-offset-input"), this);
    private final NumberFieldEditor operatorValueEditor = new NumberFieldEditor(new BaseElement(Locator.css("number-field-editor"), offsetInputField));

    private final BaseElement timeRuleResultField = new BaseElement(Locator.dataAid("time-rule-result-field"), this);
    private final Button clearValueButton = new Button(Locator.dataAid("clear-value-button"), this);

    public static final String OFFSET = "offset";
    public static final String UNIT = "unit";
    public static final String OPERATOR = "operator";
    public static final String REFERENCE_POINT = "reference_point";

    public enum ReferencePoint implements FieldEnum {
        WORKFLOW_START("workflow.start", "Workflow Start"),
        STAGE_START("stage.start", "Stage Start"),
        PARENT_ITEM_START("parent_item.start", "Parent Item Start"),
        PREVIOUS_ITEM_START("previous_item.start", "Previous Item Start"),
        WORKFLOW_END("workflow.end", "Workflow End"),
        STAGE_END("stage.end", "Stage End"),
        PARENT_ITEM_END("parent_item.end", "Parent Item End"),
        PREVIOUS_ITEM_END("previous_item.end", "Previous Item End");

        private final String value;
        private final String label;

        ReferencePoint(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    public TimeRuleDialog() {
        super(Locator.css("create-edit-time-rule-dialog"), "time-rule-", "-button");
    }

    public TimeRuleDialog cancel() {
        button("cancel").click();
        this.expectNotVisible();
        return this;
    }

    public TimeRuleDialog save() {
        button("save").click();
        this.expectNotVisible();
        return this;
    }

    public TimeRuleDialog clearValue() {
        clearValueButton.click();
        this.expectVisible();
        return this;
    }

    public TimeRuleDialog clickAddTimeInterval() {
        addTimeIntervalButton.click();
        this.expectVisible();
        return this;
    }

    public TimeRuleDialog expectAddTimeIntervalVisible(boolean visible) {
        addTimeIntervalButton.expectVisible(visible);
        return this;
    }

    public TimeRuleDialog expectStartFieldIsImmediately() {
        BaseElement startImmediatelyFieldValue = new BaseElement(Locator.dataAid(editorDisplayValueDataAid), startImmediatelyField);
        startImmediatelyFieldValue.expectTextToContain("Immediately");
        return this;
    }

    public TimeRuleDialog expectStartImmediatelyFieldIsReadOnly() {
        BaseElement input = new BaseElement(Locator.css("[class*='cols field-editor-container text-field-input']"), startImmediatelyField);
        input.expectElementToContainAttrValue("disabled", "true");
        return this;
    }

    public void expectTimeRuleResult(String value) {
        timeRuleResultField.expectTextToContain(value);
    }

    public void expectReferencePointValue(String value) {
        BaseElement referencePointValue = new BaseElement(Locator.dataAid(editorDisplayValueDataAid), referencePointField);
        referencePointValue.expectTextToContain(value);
    }

    public void expectOffsetValue(int value) {
        operatorValueEditor.expectValueToBe(value);
    }

    public void expectUnitValue(String value) {
        BaseElement unitValue = new BaseElement(Locator.dataAid(editorDisplayValueDataAid), unitSelector);
        unitValue.expectTextToContain(value);
    }

    public void expectOperatorValue(String value) {
        BaseElement operatorValue = new BaseElement(Locator.dataAid(editorDisplayValueDataAid), operatorSelector);
        operatorValue.expectTextToContain(value);
    }

    private void verifyOptionsInField(String field, List<String> listOfOptions) {
        BaseElement fieldElement = new BaseElement(Locator.dataAid(field), this);
        fieldElement.clickOn();
        BaseElement resultsList = new BaseElement(Locator.dataAid("field-editor-results-list"), this);
        BaseElement item = new BaseElement(Locator.css("li"), resultsList);
        getDriver().expects().numberOfVisibleElementsToBe(item, listOfOptions.size());
        listOfOptions.forEach(v -> new BaseElement(Locator.dataAid(v), resultsList).expectVisible());
        this.clickOn();
    }

    public void verifyOptionsInReferencePoint(List<String> listOfOptions) {
        verifyOptionsInField(REFERENCE_POINT, listOfOptions);
    }

    public void verifyOptionsInUnit() {
        verifyOptionsInField(UNIT, Arrays.asList(
                TimeUnit.MINUTES.getUnit(),
                TimeUnit.HOURS.getUnit(),
                TimeUnit.DAYS.getUnit(),
                TimeUnit.WEEKS.getUnit()));
    }

    public void verifyOptionsInOperator() {
        verifyOptionsInField(OPERATOR, Arrays.asList(
                TimeOperator.AFTER.getOperator(),
                TimeOperator.BEFORE.getOperator()));
    }

    private void selectValue(String value) {
        Element list = new BaseElement(Locator.dataAid("field-editor-results-list-container"), this);
        Element item = new BaseElement(Locator.dataAid(value), list);
        getDriver().actions().click(item);
    }

    public void setReferencePoint(ReferencePoint value) {
        referencePointField.clickOn();
        selectValue(value.getValue());
    }

    public void setOffset(int value) {
        operatorValueEditor.setValue(value);
    }

    public void setUnit(TimeUnit value) {
        unitSelector.clickOn();
        selectValue(value.getUnit());
    }

    public void setOperator(TimeOperator value) {
        operatorSelector.clickOn();
        selectValue(value.getOperator());
    }

    public void expectOffsetValidityError() {
        operatorValueEditor.expectValidityErrorToBeVisible("The field value must be an integer between 1 and 999.");
    }

    public void expectReferencePointFieldIsRequired() {
        BaseElement redIcon = new BaseElement(Locator.css("div.validity-icon > svg"), referencePointField);
        BaseElement tooltip = new BaseElement(Locator.css("tooltip[tip-text='This field is required.']"), referencePointField);
        redIcon.expectVisible();
        tooltip.isDisplayed();
    }

    public void expectReferencePointAndResultAreCleared() {
        expectReferencePointValue("");
        expectReferencePointFieldIsRequired();
        timeRuleResultField.expectNotVisible();
    }
}
