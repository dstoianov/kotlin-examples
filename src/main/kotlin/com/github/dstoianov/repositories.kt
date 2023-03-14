package com.github.dstoianov

import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<Project, Long>

interface ClientRepository : JpaRepository<Client, Long>

interface ContactRepository : JpaRepository<Contact, String>


//{
//    fun findByClient_Contacts_NameAndClient_Contacts_EmailIgnoreCase(name: String, email: String): Optional<Contact>
//    @Query("""
//            select c from Contact c inner join c.client.contacts contacts
//            where upper(contacts.email) like upper(concat('%', ?1, '%'))
//        """)
//    fun findByClient_Contacts_EmailContainsIgnoreCase(email: String?): Contact
//}