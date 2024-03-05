package com.siddhu.spring.customer;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Objects;

@Entity
@Table(
        name = "customer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "customer_name_unique",
                        columnNames = "name"
                )
        }
)
public class Customer {
    @Id
    @SequenceGenerator(
            name = "customer_id_seq",
            sequenceName = "customer_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_id_seq"
    )
    private Integer id;

    @Column(
            nullable = false
    )
    private String name;

    @Column(
            nullable = false
    )
    private Date date;

    @Column(
            nullable = false
    )
    private String gender;

    public Customer() {}
    public Customer(Integer id, String name, Date date, String gender) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.gender = gender;
    }
    public Customer(String name, Date date, String gender) {
        this.name = name;
        this.date = date;
        this.gender = gender;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
//    public Customer(String name, String email, Integer age) {
//        this.name = name;
//        this.email = email;
//        this.age = age;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(getId(), customer.getId())
                && Objects.equals(getName(), customer.getName())
                && Objects.equals(getDate(), customer.getDate())
                && Objects.equals(getGender(), customer.getGender());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDate(), getGender());
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", gender='" + gender + '\'' +
                '}';
    }
}
