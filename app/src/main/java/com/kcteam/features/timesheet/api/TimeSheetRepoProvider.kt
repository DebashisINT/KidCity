package com.kcteam.features.timesheet.api

/**
 * Created by Saikat on 29-Apr-20.
 */
object TimeSheetRepoProvider {
    fun timeSheetRepoProvider(): TimeSheetRepo {
        return TimeSheetRepo(TimeSheetApi.create())
    }

    fun timeSheetImageRepoProvider(): TimeSheetRepo {
        return TimeSheetRepo(TimeSheetApi.createImage())
    }
}