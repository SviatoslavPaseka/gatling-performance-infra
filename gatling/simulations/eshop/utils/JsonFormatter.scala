package eshop.utils

object JsonFormatter {
    def formatJson(input: String): String = {
      val indentation = "  "
      val sb = new StringBuilder
      var level = 0

      for (char <- input) {
        char match {
          case '{' | '[' =>
            sb.append(char)
            level += 1
            sb.append("\n").append(indentation * level)

          case '}' | ']' =>
            level -= 1
            sb.append("\n").append(indentation * level).append(char)

          case ',' =>
            sb.append(char)
            if (level == 0) {
              sb.append("\n").append(indentation * level)
            }

          case _ =>
            sb.append(char)
        }
      }

      sb.toString()
    }
}
