package com.example.babysitter.externalModels.boundaries;

import com.example.babysitter.externalModels.utils.CommandId;
import com.example.babysitter.externalModels.utils.CreatedBy;
import com.example.babysitter.externalModels.utils.TargetObject;

import java.util.Date;
import java.util.Map;


public class MiniAppCommandBoundary {
    private CommandId commandId;
    private String command;
    private TargetObject targetObject;
    private Date invocationTimeStamp;
    private CreatedBy invokedBy;
    private Map<String, Object> commandAttributes;

    public MiniAppCommandBoundary() {
    }

    public CommandId getCommandId() {
        return commandId;
    }

    public void setCommandId(CommandId commandId) {
        this.commandId = commandId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public TargetObject getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(TargetObject targetObject) {
        this.targetObject = targetObject;
    }

    public Date getInvocationTimeStamp() {
        return invocationTimeStamp;
    }

    public void setInvocationTimeStamp(Date invocationTimeStamp) {
        this.invocationTimeStamp = invocationTimeStamp;
    }

    public CreatedBy getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(CreatedBy invokedBy) {
        this.invokedBy = invokedBy;
    }

    public Map<String, Object> getCommandAttributes() {
        return commandAttributes;
    }

    public void setCommandAttributes(Map<String, Object> commandAttributes) {
        this.commandAttributes = commandAttributes;
    }

    @Override
    public String toString() {
        return "MiniAppCommandBoundary{" + "commandId=" + commandId + ", command='" + command + '\'' + ", targetObject="
                + targetObject + ", invocationTimeStamp=" + invocationTimeStamp + ", invokedBy=" + invokedBy
                + ", commandAttributes=" + commandAttributes + '}';
    }


}
