package com.github.dstoianov

import org.hibernate.proxy.HibernateProxy
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProjectRepositoryTest(
    @Autowired val projectRepository: ProjectRepository,
) {

    @Test
    fun projectIsInitialized() {
        val project = projectRepository.findById(1)
        assertTrue(project.isPresent)
    }

    /**
     * https://docs.oracle.com/javaee/5/tutorial/doc/bnbqa.html
     * https://docs.oracle.com/cd/E19798-01/821-1841/bnbqb/index.html
     *
     * LAZY does not work
     */
    @Test
    fun lazyLoadEnabled() {
        val project = projectRepository.findById(1).get()
        val client = project.client!!
        logger.info { "Class used for the client reference: ${client::class.java}" }
        assertTrue(HibernateProxy::class.java.isAssignableFrom(client::class.java))
    }

    /**
     * fail
     * java.lang.StackOverflowError
     *
     * https://www.baeldung.com/kotlin/data-class-equals-method
     *
     * leave id only in primary constructor
     *
     */
    @Test
    fun equalsIssue() {
        val project = projectRepository.findById(1).get()
        assertTrue(project == project.copy())
    }

    /**
     *  fail
     *   it needs implement own hashcode method
     *   how to do it, just google it by "jpa hashcode and equals"
     *
     *   https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/
     *   https://thorben-janssen.com/ultimate-guide-to-implementing-equals-and-hashcode-with-hibernate/
     */
    @Test
    fun hashCodeIsConsistent() {
        val awesomeProject = Project().apply {
            name = "Awesome project"
            client = Client(1L, "client name")
        }

        val hashSet = hashSetOf(awesomeProject)
        logger.info { "hashCode before save: " + awesomeProject.hashCode().toString() }

        projectRepository.save(awesomeProject)
        logger.info { "hashCode after save: " + awesomeProject.hashCode().toString() }

        assertTrue(awesomeProject in hashSet)
    }

    /*
    * fail
    * set id to val, add method     fun isNew() = id == null
     */
    @Test
    fun valForIdTest() {
        val project = Project().apply {
//            id = 100L
            name = "new project"
            client = Client(1L, "client name")
        }
        assertTrue(project.isNew())
        projectRepository.save(project)

        assertFalse(project.isNew())
    }

//    @Test
//    fun lateInitWorks() {
//        val project = Project().apply {
//            client = Client().apply { name = "Denys" }
//        }
//
//        assertTrue(project.client!!.name != null)
//    }


    companion object {
        private val logger = LoggerFactory.getLogger(ProjectRepositoryTest::class.java)
    }

}