package lb.quadro.basis

import java.util.function.Consumer


class QuadroReader (
    val tabSize:  Int = 8,                
    val emitGaps: Boolean = false,
    val consumer: Consumer<QuadroEntry>
) {


    // Internal state

    private var currFile: QuadroFile = QuadroFile("")

    private var currLine: QuadroLine? = null

    private val currIndent: Int  get() = currLine?.indent ?: 0


    private val textLines = ArrayList<CharSequence>()


    // Methods

    fun readStrings(path: String, strings: Sequence<CharSequence>) {
        val file = QuadroFile(path)
        currFile = file
        currLine = null
        emit(file)

        for ((index, str) in strings.withIndex()) {
            processLine(index + 1, str)
        }

        while (currLine != null) pop()
    }

    private fun processLine(lineNr: Int, str: CharSequence) {
        var n = str.length
        while (n > 0 && str[n-1].isWhitespace()) n--
        if (n == 0) {
            if (emitGaps) emit(QuadroGap(currFile, currLine ?: currFile, lineNr))
            return
        }

        val f = scanField(str, n)
        val lineStr = str.subSequence(f.offset, n)

        while (f.indent < currIndent) {
            pop()
        }

        if (f.indent == currIndent) {
            val line = QuadroLine(currFile, currLine?.parent ?: currFile, lineNr, f.indent, lineStr)
            emit(line)
        }
        else if (f.indent > currIndent) {
            val cl = currLine ?: throw RuntimeException("Internal error: no line on the stack")
            val line = QuadroLine(currFile, cl, lineNr, f.indent, lineStr)
            emit(line)
        }
        else {
            // TODO throw incorrectly indented line error
        }
    }

    private fun scanField(str: CharSequence, n: Int): Field {
        assert(n <= str.length)
        var k = 0
        var indent = 0
        while (k < n) {
            when (str[k]) {
                ' ' -> {
                    k++
                    indent++
                }
                '\t' -> {
                    k++
                    indent += tabSize - (indent % tabSize)
                }
                else -> break
            }
        }
        return Field(indent, k)
    }

    private fun push(newLine: QuadroLine) {
        assert(newLine.parent == (currLine ?: currFile))
        currLine = newLine
    }

    private fun pop(): QuadroLine {
        val cl = currLine ?: throw IllegalStateException("The stack is empty, no current line")
        currLine = cl
        // TODO emit indent if needed
        return cl
    }

    private fun emit(entry: QuadroEntry) {
        consumer.accept(entry)
        // TODO emit un-indent if needed
    }



    // Auxiliary classes

    private class Field (val indent: Int, val offset: Int) {
        init {
            assert(indent >= 0 && offset >= 0)
            assert(offset <= indent)
        }
        override fun toString() =
            if (indent == offset) "$indent"
            else "$indent($offset)"
    }

}