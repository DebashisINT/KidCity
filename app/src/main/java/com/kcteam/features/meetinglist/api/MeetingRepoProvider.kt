package com.kcteam.features.meetinglist.api

/**
 * Created by Saikat on 21-01-2020.
 */
object MeetingRepoProvider {
    fun meetingRepoProvider(): MeetingRepo {
        return MeetingRepo(MeetingApi.create())
    }
}