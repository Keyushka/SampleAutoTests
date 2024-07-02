public class PlanningPane extends BaseElement {

    private final String editorDisplayValueDataAid = "field-editor-preeditor-display-value";
    private final BaseElement infoBox = new BaseElement(Locator.dataAid("workflow-scheduling-pane-info-box"), this);
    private final BaseElement moreInfoLink = new BaseElement(Locator.className("workflow-scheduling-pane-info-box-link"), infoBox);

    private final BaseElement timeRuleField = new BaseElement(Locator.dataAid("time-rule-field"), this);
    private final BaseElement timeRuleDropDownContainer = new BaseElement(Locator.dataAid("field-editor-search-results-list-container"), timeRuleField);
    private final BaseElement timeRuleFieldValue = new BaseElement(Locator.dataAid(editorDisplayValueDataAid), timeRuleField);
    private final BaseElement timeRuleErrorMessage = new BaseElement(Locator.css(".error-message span"), timeRuleField);

    private final Button timeRuleDropDownButton = new Button(Locator.dataAid("field-editor-drop-down-button"), timeRuleField);
    private final Button timeRuleClearButton = new Button(Locator.dataAid("field-editor-clear-button"), timeRuleField);

    private final BaseElement schedulingStartTimeField = new BaseElement(Locator.dataAid("scheduling-start-time-field"), this);
    private final BaseElement schedulingDurationField = new BaseElement(Locator.dataAid("scheduling-duration-field"), this);
    private final BaseElement durationEditor = new BaseElement(Locator.dataAid("workflow-duration-editor"), schedulingDurationField);
    private final BaseElement schedulingEndTimeField = new BaseElement(Locator.dataAid("scheduling-end-time-field"), this);

    private static final String uiDateFormat = "MM/dd/yyyy HH:mm";
    private static final String schedulingStartTime = "Scheduling Start Time";
    private static final String schedulingEndTime = "Scheduling End Time";
    private static final String schedulingDuration = "Scheduling Duration";


    public PlanningPane(Element parent) {
        super(Locator.dataAid("workflow-scheduling-pane"), parent);
    }

    public PlanningPane expectInfoBoxVisible(boolean visible) {
        infoBox.expectVisible(visible);
        return this;
    }

    public PlanningPane closeInfoBox() {
        Button close = new Button(Locator.dataAid("icon-button"), infoBox);
        close.click();
        return this;
    }

    public void expectInfoBoxText(String value) {
        BaseElement infoBoxText = new BaseElement(Locator.className("typography-style--body--small"), infoBox);
        infoBoxText.expectTextToContain(value);
    }

    public void expectTimeRuleValue(String value) {
        timeRuleFieldValue.expectTextToContain(value);
    }

    public PlanningPane openTimeRuleDropDown() {
        timeRuleDropDownButton.click();
        return this;
    }

    public PlanningPane clickCreateTimeRule() {
        BaseElement createTimeRule = new BaseElement(Locator.dataAid("Create time rule"), timeRuleDropDownContainer);
        createTimeRule.clickOn();
        return this;
    }

    public PlanningPane clickEditTimeRule() {
        BaseElement editTimeRule = new BaseElement(Locator.dataAid("Edit time rule"), timeRuleDropDownContainer);
        editTimeRule.clickOn();
        return this;
    }

    public PlanningPane expectTimeRuleClearButtonVisible(boolean visible) {
        timeRuleClearButton.expectVisible(visible);
        return this;
    }

    public PlanningPane clearTimeRuleValue() {
        timeRuleField.hover();
        expectTimeRuleClearButtonVisible(true);
        timeRuleClearButton.click();
        return this;
    }

    private PlanningPane expectTimeRuleDropDownButtonVisible(boolean visible) {
        timeRuleDropDownButton.expectVisible(visible);
        return this;
    }

    public PlanningPane expectTimeRuleIsReadOnly() {
        return expectTimeRuleDropDownButtonVisible(false);
    }

    public PlanningPane expectTimeRuleIsEditable() {
        return expectTimeRuleDropDownButtonVisible(true);
    }

    public PlanningPane verifyTimeRuleWarningMessage(String expectedWarningMessage) {
        timeRuleErrorMessage.hover().expectVisible();
        String actualWarningMessage = timeRuleErrorMessage.getAttributeValue("title");
        Assert.assertEquals("Wrong :", expectedWarningMessage, actualWarningMessage);
        return this;
    }

    private Button getPinIcon(BaseElement dateField) {
        return new Button(Locator.dataAid("icon-button"), dateField);
    }

    public PlanningPane expectPinIconsVisibleForAllDateFields() {
        getPinIcon(schedulingStartTimeField).expectVisible();
        getPinIcon(schedulingDurationField).expectVisible();
        getPinIcon(schedulingEndTimeField).expectVisible();
        return this;
    }

    private boolean isPinned(Button button) {
        return button.getAttributeValue("class").contains("activated");
    }

    private boolean isDisabled(Button button) {
        return button.getAttributeValue("disabled") != null; //read-only
    }

    private void expectFieldPinned(BaseElement dateField, String fieldName, boolean pinned) {
        Button pinIcon = getPinIcon(dateField);
        if (isPinned(pinIcon) != pinned) {
            throw new AssertionError("The pin icon for the " + fieldName + " is " + (pinned ? "not activated" : "activated") + " in the Scheduling pane.");
        }
    }

    private void expectFieldDisabled(BaseElement dateField, String fieldName, boolean disabled) {
        Button pinIcon = getPinIcon(dateField);
        if (isDisabled(pinIcon) != disabled) {
            throw new AssertionError("The pin icon for the " + fieldName + " is " + (disabled ? "not disabled" : "disabled") + " in the Scheduling pane.");
        }
    }

    public void expectSchedulingStartTimePinned(boolean pinned) {
        expectFieldPinned(schedulingStartTimeField, schedulingStartTime, pinned);
    }

    public void expectSchedulingEndTimePinned(boolean pinned) {
        expectFieldPinned(schedulingEndTimeField, schedulingEndTime, pinned);
    }

    public void expectSchedulingDurationPinned(boolean pinned) {
        expectFieldPinned(schedulingDurationField, schedulingDuration, pinned);
    }

    public void expectSchedulingStartTimeDisabled(boolean disabled) {
        expectFieldDisabled(schedulingStartTimeField, schedulingStartTime, disabled);
    }

    public void expectSchedulingEndTimeDisabled(boolean disabled) {
        expectFieldDisabled(schedulingEndTimeField, schedulingEndTime, disabled);
    }

    public void expectSchedulingDurationDisabled(boolean disabled) {
        expectFieldDisabled(schedulingDurationField, schedulingDuration, disabled);
    }

    public PlanningPane pinSchedulingStartTime() {
        return pin(getPinIcon(schedulingStartTimeField), schedulingStartTime);
    }

    public PlanningPane unpinSchedulingStartTime() {
        return unpin(getPinIcon(schedulingStartTimeField), schedulingStartTime);
    }

    public PlanningPane pinSchedulingDuration() {
        return pin(getPinIcon(schedulingDurationField), schedulingDuration);
    }

    public PlanningPane unpinSchedulingDuration() {
        return unpin(getPinIcon(schedulingDurationField), schedulingDuration);
    }

    public PlanningPane pinSchedulingEndTime() {
        return pin(getPinIcon(schedulingEndTimeField), schedulingEndTime);
    }

    public PlanningPane unpinSchedulingEndTime() {
        return unpin(getPinIcon(schedulingEndTimeField), schedulingEndTime);
    }

    private PlanningPane pin(Button button, String fieldName) {
        if (isPinned(button)) {
            throw new IllegalStateException("The " + fieldName + " is already pinned.");
        }
        button.click();
        return this;
    }

    private PlanningPane unpin(Button button, String fieldName) {
        if (!isPinned(button)) {
            throw new IllegalStateException("The " + fieldName + " is already unpinned.");
        }
        button.click();
        return this;
    }

    private BaseElement getCalendarIcon(BaseElement dateField) {
        return new BaseElement(Locator.css("svg[name='icon-calendar']"), dateField);
    }

    private DatePicker getDateTimePicker(BaseElement dateField) {
        return new DatePicker(Locator.dataAid("alm-date-time-editor"), dateField); // DatePicker
    }

    private void clickCalendarSchedulingDateField(BaseElement dateField) {
        getCalendarIcon(dateField).clickOn();
        getDateTimePicker(dateField).expectVisible();
    }

    public void clickCalendarSchedulingStartTime() {
        clickCalendarSchedulingDateField(schedulingStartTimeField);
    }

    public void clickCalendarSchedulingEndTime() {
        clickCalendarSchedulingDateField(schedulingEndTimeField);
    }

    public void clickSchedulingDuration() {
        schedulingDurationField.clickOn();
        durationEditor.expectVisible();
    }

    public PlanningPane expectCalendarIconVisibleOnlyForStartEndFields() {
        getCalendarIcon(schedulingStartTimeField).expectVisible();
        getCalendarIcon(schedulingDurationField).expectNotVisible();
        getCalendarIcon(schedulingEndTimeField).expectVisible();
        return this;
    }

    public void setSchedulingDateTimeField(BaseElement dateField, Calendar calendar) {
        clickCalendarSchedulingDateField(dateField);
        DatePicker datePicker = new DatePicker(dateField);
        datePicker.setValue(calendar);
        WorkflowUtils.closeSuccessNotificationIfAppears();
        datePicker.expectNotVisible();
    }

    public void setSchedulingStartTime(Calendar calendar) {
        setSchedulingDateTimeField(schedulingStartTimeField, calendar);
    }

    public void setSchedulingEndTime(Calendar calendar) {
        setSchedulingDateTimeField(schedulingEndTimeField, calendar);
    }

    public void setSchedulingDuration(String duration) {
        schedulingDurationField.clickOn();
        durationEditor.expectVisible();
        durationEditor.setValue(duration);
        WorkflowUtils.closeSuccessNotificationIfAppears();
        durationEditor.expectNotVisible();
    }
}
