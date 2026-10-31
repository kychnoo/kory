package io.kory.core.extension.tool

import io.kory.core.tool.ChatTool
import io.kory.core.tool.KoryTool

fun KoryTool<*, *>.asChatTool(): ChatTool = ChatTool.Local(this)