package com.example.edric.blocksapp;

import android.provider.BaseColumns;

public final class DbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DbContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TASK_TABLE_NAME = "tasks";
        //public static final String TASK_COLUMN_ID = "id";
        public static final String TASK_COLUMN_NAME = "name";
        public static final String TASK_COLUMN_TIME_REMAINING = "time_remaining";
        public static final String TASK_COLUMN_TIME_SPENT = "time_spent";
        public static final String TASK_COLUMN_LIFETIME = "lifetime";
    }

    public static class StatusEntry implements BaseColumns {
        public static final String STATUS_TABLE_NAME = "status";
        public static final String STATUS_COLUMN_DESTROY_DATE = "destroy_date";
        public static final String STATUS_COLUMN_DESTROY_TIME_MIN = "destroy_time";
        public static final String STATUS_COLUMN_ACTIVE_TASK = "active_task";
        public static final String STATUS_COLUMN_BREAK_TIME = "break_time";
        public static final String STATUS_COLUMN_TIME_PERIOD = "time_period";
    }

    public static class TaskGroupEntry implements BaseColumns {
        public static final String GROUP_TABLE_NAME = "group_tbl";
        public static final String GROUP_COLUMN_PLAN_ID = "group_id";
        public static final String GROUP_COLUMN_TASK_ID = "task_id";
        public static final String GROUP_COLUMN_TASK_TIME = "task_time";
    }
}
