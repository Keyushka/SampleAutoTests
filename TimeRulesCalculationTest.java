public class TimeRulesCalculationTest extends AutomationTest {
    protected Workflow.Data workflow;
    protected WorkflowStage.Data stageSequential, stageParallel;
    protected WorkflowGroup.Data<?> groupSequential, groupParallel;
    protected WorkflowAutoTask.Data<?> autoTask;
    private WorkflowTask.Data<?> manualTask1, manualTask2;
    private WorkflowQualityCriteria.Data<?> qualityCriteria1, qualityCriteria2;
    private static final String uiDateFormat = "MM/dd/yyyy HH:mm";

    @Test
    public void previousItemForFirstItemIsTheStartTimeOfTheParent() {
        reporter.testStep("Prepare Workflow with Stage/Group (parallel/sequential) that have first workflow item with time rule = 'Start 2 days after start/end of previous item')");
        WorkflowTimeRuleDTO start2DaysAfterEndOfPreviousItem = WorkflowBase.Rest.getReferenceRule(
                2, ReferenceRuleUnit.DAYS, ReferenceRuleOperator.AFTER, ReferenceRuleField.END, ReferenceRuleType.PREVIOUS_ITEM);
        WorkflowTimeRuleDTO start2DaysAfterStartOfPreviousItem = WorkflowBase.Rest.getReferenceRule(
                2, ReferenceRuleUnit.DAYS, ReferenceRuleOperator.AFTER, ReferenceRuleField.START, ReferenceRuleType.PREVIOUS_ITEM);
        workflow = workflow.Rest.create();

        stageSequential = WorkflowStage.Rest.create(workflow);
        manualTask1 = new WorkflowTask.Data<>(stageSequential);
        manualTask1.setTimeRule(start2DaysAfterEndOfPreviousItem);
        manualTask1 = WorkflowTask.Rest.create(manualTask1);
        groupParallel = WorkflowGroup.Rest.createParallel(stageSequential);
        qualityCriteria1 = new WorkflowQualityCriteria.Data<>(groupParallel);
        qualityCriteria1.setTimeRule(start2DaysAfterStartOfPreviousItem);
        qualityCriteria1 = WorkflowQualityCriteria.Rest.create(qualityCriteria1);

        stageParallel = WorkflowStage.Rest.create(workflow);
        groupSequential = new WorkflowGroup.Data<>(stageParallel);
        groupSequential.setTimeRule(start2DaysAfterEndOfPreviousItem);
        groupSequential = WorkflowGroup.Rest.create(groupSequential);
        manualTask2 = new WorkflowTask.Data<>(groupSequential);
        manualTask2.setTimeRule(start2DaysAfterStartOfPreviousItem);
        manualTask2 = WorkflowTask.Rest.create(manualTask2);
        qualityCriteria2 = WorkflowQualityCriteria.Rest.create(groupSequential);

        reporter.testStep("Pin Duration/End time of the Stage/Group");
        WorkflowStage.Rest.updateField(stageSequential, WorkflowStage.Fields.IS_SCHEDULED_DURATION_PINNED, true);
        WorkflowStage.Rest.updateField(groupParallel, WorkflowStage.Fields.IS_SCHEDULED_END_TIME_PINNED, true);
        WorkflowStage.Rest.updateField(stageParallel, WorkflowStage.Fields.IS_SCHEDULED_END_TIME_PINNED, true);
        WorkflowStage.Rest.updateField(groupSequential, WorkflowStage.Fields.IS_SCHEDULED_DURATION_PINNED, true);

        GregorianCalendar stageSequentialStartTime = stageSequential.getStartTime();
        GregorianCalendar groupParallelStartTime = groupParallel.getStartTime();
        GregorianCalendar stageParallelStartTime = stageParallel.getStartTime();
        GregorianCalendar groupSequentialStartTime = groupSequential.getStartTime();

        navigateToEntity(workflow);
        reporter.testStep("For the first workflow item of Stage/Group, verify that 'start time' = '<container> start time + 2 days' and 'time rule compliance' = 'Yes'");
        UI.views.workflowView.workflowItemsContainer.toolbar.addColumnsAndCloseChooser(Arrays.asList(WorkflowBase.Fields.START_TIME_RULE, WorkflowBase.Fields.START_TIME_RULE_COMPLIANCE));
        expectStartTimeForItems(2, Arrays.asList(manualTask1, qualityCriteria1, groupSequential, manualTask2),
                Arrays.asList(stageSequentialStartTime, groupParallelStartTime, stageParallelStartTime, groupSequentialStartTime));
        expectTimeRuleComplianceForItems(Arrays.asList(manualTask1, qualityCriteria1, groupSequential, manualTask2), WorkflowBase.TimeRuleCompliance.YES.getLabel());

        reporter.testStep("For Stage/Group, pin Start time -3 days");
        UI.views.workflowView.workflowItemsContainer.setDateTimeField(-3, stageSequential, WorkflowBase.Fields.START_TIME, stageSequentialStartTime);
        UI.views.workflowView.workflowItemsContainer.setDateTimeField(-3, groupParallel, WorkflowBase.Fields.START_TIME, groupParallelStartTime);
        UI.views.workflowView.workflowItemsContainer.setDateTimeField(-3, stageParallel, WorkflowBase.Fields.START_TIME, stageParallelStartTime);
        UI.views.workflowView.workflowItemsContainer.setDateTimeField(-3, groupSequential, WorkflowBase.Fields.START_TIME, groupSequentialStartTime);

        stageSequential = WorkflowStage.Rest.getEntityWithFields(stageSequential);
        groupParallel = WorkflowGroup.Rest.getEntityWithFields(groupParallel);
        stageParallel = WorkflowStage.Rest.getEntityWithFields(stageParallel);
        groupSequential = WorkflowGroup.Rest.getEntityWithFields(groupSequential);

        GregorianCalendar stageSequentialStartTimeMinus3Days = stageSequential.getStartTime();
        GregorianCalendar groupParallelStartTimeMinus3Days = groupParallel.getStartTime();
        GregorianCalendar stageParallelStartTimeMinus3Days = stageParallel.getStartTime();
        GregorianCalendar groupSequentialStartTimeMinus3Days = groupSequential.getStartTime();

        reporter.testStep("For the first workflow item of Stage/Group, verify that 'start time' = '<container> start time + 2 days' and 'start time rule compliance' = 'Yes'");
        expectStartTimeForItems(2, Arrays.asList(manualTask1, qualityCriteria1, groupSequential, manualTask2),
                Arrays.asList(stageSequentialStartTimeMinus3Days, groupParallelStartTimeMinus3Days, stageParallelStartTimeMinus3Days, groupSequentialStartTimeMinus3Days));
        expectTimeRuleComplianceForItems(Arrays.asList(manualTask1, qualityCriteria1, groupSequential, manualTask2), WorkflowBase.TimeRuleCompliance.YES.getLabel());
    }

    @Test
    public void endTimeOfSequentialContainerIsTheEndTimeOfLastItem() {
        reporter.testStep("Prepare Sequential Stage_1 and Stage_2 with several items)");
        workflow = workflow.Rest.create();

        stageSequential = WorkflowStage.Rest.create(workflow);
        manualTask1 = WorkflowTask.Rest.create(stageSequential);
        qualityCriteria1 = WorkflowQualityCriteria.Rest.create(stageSequential);

        WorkflowStage.Data stageSequential2 = new WorkflowStage.Data(workflow);
        stageSequential2.setTimeRule(WorkflowBase.Rest.getReferenceRule(
                3, ReferenceRuleUnit.DAYS, ReferenceRuleOperator.AFTER, ReferenceRuleField.END, ReferenceRuleType.PREVIOUS_ITEM));
        stageSequential2 = WorkflowStage.Rest.create(stageSequential2);
        manualTask2 = WorkflowTask.Rest.create(stageSequential2);

        stageSequential = WorkflowStage.Rest.getEntityWithFields(stageSequential);
        GregorianCalendar stageSequentialEndTime = stageSequential.getEndTime();
        GregorianCalendar qualityCriteria1EndTime = qualityCriteria1.getEndTime();

        navigateToEntity(workflow);
        reporter.testStep("For Stage_2, verify that 'start time' = 'Stage_1 end time + 3 days = last item Stage_1 end time + 3 days' and 'start time rule compliance' = 'Yes'");
        UI.views.workflowView.workflowItemsContainer.toolbar.addColumnsAndCloseChooser(Arrays.asList(WorkflowBase.Fields.START_TIME_RULE, WorkflowBase.Fields.START_TIME_RULE_COMPLIANCE));
        expectStartTimeForItems(3, Arrays.asList(stageSequential2, stageSequential2), Arrays.asList(stageSequentialEndTime, qualityCriteria1EndTime));
        UI.views.workflowView.workflowItemsContainer.expectStartTimeRuleCompliance(stageSequential2, WorkflowBase.TimeRuleCompliance.YES.getLabel());

        reporter.testStep("For last item Stage_1 (qualityCriteria1), pin end time + 2 days");
        UI.views.workflowView.workflowItemsContainer.setDateTimeField(2, qualityCriteria1, WorkflowBase.Fields.END_TIME, qualityCriteria1EndTime);
        stageSequential = WorkflowStage.Rest.getEntityWithFields(stageSequential);
        qualityCriteria1 = WorkflowQualityCriteria.Rest.getEntityWithFields(qualityCriteria1);
        stageSequentialEndTime = stageSequential.getEndTime();
        GregorianCalendar qualityCriteria1EndTimePlus2Days = qualityCriteria1.getEndTime();
        Assert.assertEquals("Stage_1 end time not equal last item Stage_1 end time", stageSequentialEndTime, qualityCriteria1EndTimePlus2Days);
        UI.views.workflowView.workflowItemsContainer.grid.getCell(stageSequential2, WorkflowTask.Fields.START_TIME).expectValueContains(getDateTimeString(3, stageSequentialEndTime, uiDateFormat));

        reporter.testStep("For Stage_1, pin duration + 1 days");
        stageSequential = WorkflowStage.Rest.updatePlannedDuration(stageSequential, "0w 3d 0h 0m"); //+1d
        GregorianCalendar stageSequentialEndTimePlus1Day = stageSequential.getEndTime();
        UI.views.workflowView.workflowItemsContainer.toolbar.clickRefresh();
        UI.views.workflowView.workflowItemsContainer.grid.getCell(stageSequential2, WorkflowTask.Fields.START_TIME).expectValueContains(getDateTimeString(3, stageSequentialEndTimePlus1Day, uiDateFormat));
        reporter.testStep("For Stage_1, pin end time - 3 days");
        UI.views.workflowView.workflowItemsContainer.setDateTimeField(-3, stageSequential, WorkflowBase.Fields.END_TIME, stageSequentialEndTimePlus1Day);
        stageSequential = WorkflowStage.Rest.getEntityWithFields(stageSequential);
        GregorianCalendar stageSequentialEndTimeMinus3Days = stageSequential.getEndTime();
        UI.views.workflowView.workflowItemsContainer.toolbar.clickRefresh();
        UI.views.workflowView.workflowItemsContainer.grid.getCell(stageSequential2, WorkflowTask.Fields.START_TIME).expectValueContains(getDateTimeString(3, stageSequentialEndTimeMinus3Days, uiDateFormat));
        UI.views.workflowView.workflowItemsContainer.expectStartTimeRuleCompliance(stageSequential2, WorkflowBase.TimeRuleCompliance.YES.getLabel());
    }

    @Test
    public void endTimeOfParallelContainerIsTheEndTimeOfLongestItem() {
        reporter.testStep("Prepare Parallel Stage and Group with 2 workflow item with a defined time rule)");
        WorkflowTimeRuleDTO start1DayAfterEndOfPreviousItem = WorkflowBase.Rest.getReferenceRule(
                1, ReferenceRuleUnit.DAYS, ReferenceRuleOperator.AFTER, ReferenceRuleField.END, ReferenceRuleType.PREVIOUS_ITEM);
        WorkflowTimeRuleDTO start1WeeksAfterStartOfParentItem = WorkflowBase.Rest.getReferenceRule(
                1, ReferenceRuleUnit.WEEKS, ReferenceRuleOperator.AFTER, ReferenceRuleField.START, ReferenceRuleType.PARENT_ITEM);
        workflow = workflow.Rest.create();

        stageParallel = WorkflowStage.Rest.createParallel(workflow);
        manualTask1 = new WorkflowTask.Data<>(stageParallel);
        manualTask1.setTimeRule(start1DayAfterEndOfPreviousItem);
        manualTask1 = WorkflowTask.Rest.create(manualTask1);
        qualityCriteria1 = new WorkflowQualityCriteria.Data<>(stageParallel);
        qualityCriteria1.setTimeRule(start1WeeksAfterStartOfParentItem);
        qualityCriteria1 = WorkflowQualityCriteria.Rest.create(qualityCriteria1);

        stageSequential = WorkflowStage.Rest.create(workflow);
        groupParallel = WorkflowGroup.Rest.createParallel(stageSequential);
        manualTask2 = new WorkflowTask.Data<>(groupParallel);
        manualTask2.setTimeRule(start1DayAfterEndOfPreviousItem);
        manualTask2 = WorkflowTask.Rest.create(manualTask2);
        qualityCriteria2 = new WorkflowQualityCriteria.Data<>(groupParallel);
        qualityCriteria2.setTimeRule(start1WeeksAfterStartOfParentItem);
        qualityCriteria2 = WorkflowQualityCriteria.Rest.create(qualityCriteria2);

        stageParallel = WorkflowStage.Rest.getEntityWithFields(stageParallel);
        groupParallel = WorkflowGroup.Rest.getEntityWithFields(groupParallel);
        GregorianCalendar stageParallelStartTime = stageParallel.getStartTime();
        GregorianCalendar groupParallelStartTime = groupParallel.getStartTime();

        navigateToEntity(workflow);
        reporter.testStep("For Stage/Group, end time = start time + 1w 1d");
        UI.views.workflowView.workflowItemsContainer.grid.getCell(stageParallel, WorkflowBase.Fields.END_TIME).expectValueContains(getDateTimeString(8, stageParallelStartTime, uiDateFormat));
        UI.views.workflowView.workflowItemsContainer.grid.getCell(groupParallel, WorkflowBase.Fields.END_TIME).expectValueContains(getDateTimeString(8, groupParallelStartTime, uiDateFormat));
        expectStartTimeForItems(1, Arrays.asList(manualTask1, manualTask2), Arrays.asList(stageParallelStartTime, groupParallelStartTime));

        reporter.testStep("Pin Duration of the Stage and pin End time of the Group");
        WorkflowStage.Rest.updateField(stageParallel, WorkflowStage.Fields.IS_SCHEDULED_DURATION_PINNED, true);
        WorkflowStage.Rest.updateField(groupParallel, WorkflowStage.Fields.IS_SCHEDULED_END_TIME_PINNED, true);
        UI.views.workflowView.workflowItemsContainer.toolbar.clickRefresh();
        expectStartTimeForItems(1, Arrays.asList(manualTask1, manualTask2), Arrays.asList(stageParallelStartTime, groupParallelStartTime));
    }

    private void expectTimeRuleComplianceForItems(List<WorkflowBase.Data> itemsToCheck, String timeRuleComplianceValue) {
        for (WorkflowBase.Data item : itemsToCheck) {
            UI.views.workflowView.workflowItemsContainer.expectStartTimeRuleCompliance(item, timeRuleComplianceValue);
        }
    }

    private void expectStartTimeForItems(int days, List<WorkflowBase.Data> itemsToCheck, List<GregorianCalendar> startTimes) {
        for (int i = 0; i < itemsToCheck.size(); i++) {
            WorkflowBase.Data item = itemsToCheck.get(i);
            GregorianCalendar startTime = startTimes.get(i);
            UI.views.workflowView.workflowItemsContainer.grid.getCell(item, WorkflowBase.Fields.START_TIME).expectValueContains(getDateTimeString(days, startTime, uiDateFormat));
        }
    }
}
