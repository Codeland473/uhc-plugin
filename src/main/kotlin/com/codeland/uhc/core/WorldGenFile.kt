package com.codeland.uhc.core

import com.codeland.uhc.util.Util
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object WorldGenFile {
	class WorldGenInfo(var netherFix: Boolean, var mushroomFix: Boolean, var oreFix: Boolean, var melonFix: Boolean)

	val filename = "uhc.properties"

	val properties = arrayOf(
		"nether-fix",
		"mushroom-fix",
		"ore-fix",
		"melon-fix"
	)

	fun getSettings(): WorldGenInfo {
		val file = File(filename)

		return if (file.exists()) {
			val reader = FileReader(file)

			val values = Array(properties.size) { true }

			reader.forEachLine { line ->
				if (!line.startsWith("#")) {
					for (i in properties.indices) {
						if (line.startsWith(properties[i])) {
							val equalsIndex = line.indexOf('=')

							if (equalsIndex != -1 && equalsIndex != line.lastIndex)
								values[i] = line.substring(equalsIndex + 1).toBoolean()

							break
						}
					}
				}
			}

			WorldGenInfo(values[0], values[1], values[2], values[3])
		} else {
			createDefaultFile()
			WorldGenInfo(true, true, true, true)
		}
	}

	fun createDefaultFile() {
		val writer = FileWriter(File(filename))

		writer.write("#UHC World Generator Settings\n#Regenerate the world for changes to take effect\n")

		properties.forEach { property ->
			writer.write("$property=true\n")
		}

		writer.close()
	}
}
