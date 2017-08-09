package com.novp.sprytar.events;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.novp.sprytar.data.model.FamilyMember;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FamilyMemberEvent {

    @IntDef({DELETE, UPDATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int DELETE = 0;

    public static final int UPDATE = 1;

    @Type
    private int actionType;
    private int position;
    private boolean confirmed;
    private int id;
    private boolean newMember;
    private FamilyMember familyMember;

    private FamilyMemberEvent(@Type int actionType, int position, boolean confirmed, int id, boolean
            newMember, FamilyMember familyMember) {
        this.actionType = actionType;
        this.position = position;
        this.confirmed = confirmed;
        this.id = id;
        this.newMember = newMember;
        this.familyMember = familyMember;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(@Type int actionType) {
        this.actionType = actionType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNewMember() {
        return newMember;
    }

    public void setNewMember(boolean newMember) {
        this.newMember = newMember;
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(FamilyMember familyMember) {
        this.familyMember = familyMember;
    }

    public static class Builder {
        private int actionType;
        private int position;
        private boolean confirmed;
        private int id;
        private boolean newMember;
        private FamilyMember familyMember;

        public Builder setActionType(@NonNull @Type int actionType) {
            this.actionType = actionType;
            return this;
        }

        public Builder setPosition(@NonNull int position) {
            this.position = position;
            return this;
        }

        public Builder setConfirmed(@NonNull boolean confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public Builder setId(@NonNull int id) {
            this.id = id;
            return this;
        }

        public Builder setNewMember(@NonNull boolean newMember) {
            this.newMember = newMember;
            return this;
        }

        public Builder setFamilyMember(@NonNull FamilyMember familyMember) {
            this.familyMember = familyMember;
            return this;
        }

        public FamilyMemberEvent build(){
            return  new FamilyMemberEvent(actionType, position, confirmed, id, newMember, familyMember);
        }

    }
}
