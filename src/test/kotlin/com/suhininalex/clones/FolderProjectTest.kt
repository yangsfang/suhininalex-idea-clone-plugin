package com.suhininalex.clones

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.ElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.suhininalex.clones.core.*
import com.suhininalex.clones.core.languagescope.java.JavaIndexedSequence
import com.suhininalex.clones.core.utils.findTokens
import org.junit.Ignore
import kotlin.properties.Delegates

@Ignore("WIP")
open class FolderProjectTest(val testFolder: String) : LightJavaCodeInsightFixtureTestCase() {

    var baseDirectoryPsi by Delegates.notNull<PsiDirectory>()

    override fun getTestDataPath() = testFolder

    override fun setUp() {
        super.setUp()
        val directory = myFixture.copyDirectoryToProject("/", "")
        baseDirectoryPsi = myFixture.psiManager.findDirectory(directory)!!
//        baseDirectoryPsi.findTokens(TokenSet.create(ElementType.METHOD)).forEach { method ->
//            if (method is PsiMethod)
//                CloneIndexer.addSequence(JavaIndexedSequence(method))
//        }
    }
}