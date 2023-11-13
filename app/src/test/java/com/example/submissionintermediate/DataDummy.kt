package com.example.submissionintermediate

import com.example.submissionintermediate.data.response.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "createdAt",
                "name",
                "description",
                "id",
                i.toDouble(),
                i.toDouble()

            )
            items.add(story)
        }
        return items
    }
}