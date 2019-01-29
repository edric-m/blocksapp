package com.example.edric.blocksapp;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TASK_TABLE_NAME = "tasks";
        public static final String TASK_COLUMN_ID = "id";
        public static final String TASK_COLUMN_NAME = "name";
        public static final String TASK_COLUMN_TIME_REMAINING = "time_remaining";
        public static final String TASK_COLUMN_TIME_SPENT = "time_spent";
        public static final String TASK_COLUMN_LIFETIME = "lifetime";
    }

    public
}
