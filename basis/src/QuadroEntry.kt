package lb.quadro.basis


sealed class QuadroEntry {
}


class QuadroFile: QuadroEntry {

    @JvmField
    val path: String

    constructor(path: String) {
        this.path = path
    }

}


class QuadroLine: QuadroEntry {

    @JvmField
    val file: QuadroFile

    @JvmField
    val parent: QuadroEntry

    @JvmField
    val lineNr: Int

    @JvmField
    val indent: Int

    @JvmField
    val str: CharSequence

    constructor(file: QuadroFile, parent: QuadroEntry, lineNr: Int, indent: Int, str: CharSequence): super() {
        this.file = file
        this.parent = parent
        this.lineNr = lineNr
        this.indent = indent
        this.str = str
    }

}


class QuadroGap: QuadroEntry {

    @JvmField
    val file: QuadroFile

    @JvmField
    val parent: QuadroEntry

    @JvmField
    val lineNr: Int

    constructor(file: QuadroFile, parent: QuadroEntry, lineNr: Int): super() {
        this.file = file
        this.parent = parent
        this.lineNr = lineNr
    }
    
}


class QuadroText: QuadroEntry {

    @JvmField
    val parentLine: QuadroLine

    @JvmField
    val firstLineNr: Int

    @JvmField
    val indent: Int

    @JvmField
    val text: CharSequence

    constructor(parentLine: QuadroLine, firstLineNr: Int, indent: Int, text: CharSequence): super() {
        this.parentLine = parentLine
        this.firstLineNr = firstLineNr
        this.indent = indent
        this.text = text
    }
    
}
