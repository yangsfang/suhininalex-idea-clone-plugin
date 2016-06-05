package com.suhininalex.clones.core

import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.suhininalex.clones.ide.ProjectClonesInitializer
import com.suhininalex.suffixtree.Edge
import com.suhininalex.suffixtree.Node
import iterate
import stream
import java.awt.EventQueue
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport

fun PsiMethod.getStringId() =
        containingFile.containingDirectory.name + "." +
        containingClass!!.name + "." +
        name + "."+
        parameterList;

val Edge.length: Int
    get() = end - begin + 1

fun Clone.getTextRangeInMethod() = TextRange(firstElement.getTextRange().startOffset, lastElement.getTextRange().endOffset)

fun Project.getCloneManager() = ProjectClonesInitializer.getInstance(this)

fun Token.getTextRange() = source.textRange

fun Node.lengthToRoot() =
    riseTraverser().sumBy { it.parentEdge?.length ?: 0 }

fun <T> callInEventQueue(body: ()->T): T {
    if (EventQueue.isDispatchThread()) return body()
    var result: T? = null
    EventQueue.invokeAndWait { result = body() }
    return result!!
}

val Application: Application
    get() = ApplicationManager.getApplication()


fun Node.riseTraverser() = object: Iterable<Node> {
    var node: Node? = this@riseTraverser
    override fun iterator() = iterate {
        val result = node
        node = node?.parentEdge?.parent
        result
    }
}

fun Node.descTraverser() = riseTraverser().reversed()

fun <T> Iterator<T>?.hasNext() = if (this!=null) hasNext() else false

fun <T> Iterable<T>.isEmpty() = !iterator().hasNext()

fun <T> Stream<T>.isEmpty() = iterator().hasNext()

fun <T> Stream<T>.toList(): List<T> = collect(Collectors.toList()!!)

fun <T> Iterator<T>.nextOrNull() = if (hasNext()) next() else null

fun <T> Stream<out T>.concat(stream: Stream<out T>) = Stream.concat(this, stream)

fun <T> T.depthFirstTraverse(children: (T) -> Stream<T>): Stream<T> =
    Stream.of(this).concat( children(this).flatMap { it.depthFirstTraverse(children) } )

fun <T> T.depthFirstTraverse(recursionFilter: (T)-> Boolean, children: (T) -> Stream<T>) =
    this.depthFirstTraverse { if (recursionFilter(it)) children(it) else Stream.empty() }

fun <T> T.leafTraverse(isLeaf: (T)-> Boolean, children: (T) -> Stream<T>) =
    this.depthFirstTraverse ({ ! isLeaf(it) }, children).filter { isLeaf(it) }

fun Project.getAllPsiJavaFiles() =
    PsiManager.getInstance(this).findDirectory(baseDir)!!.getPsiJavaFiles()

fun PsiDirectory.getPsiJavaFiles(): Stream<PsiJavaFile> =
    this.depthFirstTraverse { it.subdirectories.stream() }.flatMap { it.files.stream() }.filter { it is PsiJavaFile }.map { it as PsiJavaFile }

fun PsiElement.findTokens(filter: TokenSet): Stream<PsiElement> =
    this.leafTraverse({it in filter}) {it.children.stream()}

operator fun TokenSet.contains(element: PsiElement?): Boolean = this.contains(element?.node?.elementType)

fun PsiElement.asStream(filter: TokenSet): Stream<PsiElement> =
    this.depthFirstTraverse ({it !in filter}) { it.children.stream() } .filter { it !in filter }

fun <T> times(times: Int, provider: ()-> Stream<T>) =
    (1..times).stream().flatMap { provider() }

fun <T1,T2> zip(first: Iterator<T1>, second: Iterator<T2>) = iterate {
    if (first.hasNext() && second.hasNext()) Pair(first.next(), second.next()) else null
}

fun <T1, T2> zip(first: Stream<T1>, second: Stream<T2>) =
    zip(first.iterator(), second.iterator()).stream()

fun <T> Iterator<T>.stream() = StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false)

infix fun <T> Stream<T>.equalContent(another: Stream<T>) =
    zip(this, another).allMatch { it.first == it.second }

fun CloneClass.tokenStream() =
    treeNode.descTraverser().stream().map { it.parentEdge }.filter { it != null }.flatMap { it.asSequence().stream() }

fun Edge.asSequence() = sequence.subList(begin, end + 1) as List<Token>

fun <T> Stream<T>.forEachIndexed(f: (Int, T) -> Unit) {
    var i = 0
    this.forEach { f(i++, it) }
}

fun <T> Stream<T>.peekIndexed(f: (Int, T) -> Unit): Stream<T> {
    var i = 0
    return this.peek { f(i++, it) }
}

val javaTokenFilter = TokenSet.create(
        ElementType.WHITE_SPACE, ElementType.SEMICOLON, ElementType.RBRACE, ElementType.LBRACE, ElementType.DOC_COMMENT, ElementType.C_STYLE_COMMENT, ElementType.END_OF_LINE_COMMENT,ElementType.RPARENTH, ElementType.LPARENTH, ElementType.RBRACE, ElementType.LBRACE
    )

fun Stream<Token>.print(){
    forEach { print("${it.source.node.elementType} ") }
    println()
}