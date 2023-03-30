package com.github.dstoianov

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import org.hibernate.Hibernate
import org.hibernate.annotations.NaturalId


//open /* we do not need open here we have enabled plugin */
@Table(name = "project")
@Entity
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,
) {

    fun isNew() = id == null

    @Column(name = "name", nullable = false)
    var name: String? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    var client: Client? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Project

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

}

@Table(name = "client")
@Entity
data class Client(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @OneToMany(mappedBy = "client", orphanRemoval = true)
    var projects: MutableSet<Project> = mutableSetOf(),

    @OneToMany(mappedBy = "client")
    var contacts: MutableSet<Contact> = mutableSetOf()
)

@Table(name = "contact")
@Entity
data class Contact(
    /**
     * If the entity has a natural or client-generated id, it makes sense to put it in the constructor,
     * so the object cannot be created without it.
     *
     * Since it does not change during the entity lifecycle, it can be used in equals() and hashCode().
     */
    @Id
    @NaturalId
    @Column(name = "email", nullable = false)
    val email: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    var client: Client,
)
