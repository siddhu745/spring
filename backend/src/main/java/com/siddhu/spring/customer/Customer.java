package com.siddhu.spring.customer;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "customer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "customer_name_unique",
                        columnNames = "name"
                ),
                @UniqueConstraint(
                        name = "profile_image_id_unique",
                        columnNames = "profileImageId"
                )
        }
)
public class Customer implements UserDetails {
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

    @Column(
            nullable = false
    )
    private String password;

    @Column(
            unique = true
    )
    private String profileImageId;

    public Customer() {}
    public Customer(Integer id,
                    String name,
                    String password,
                    Date date,
                    String gender) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.date = date;
        this.gender = gender;
    }

    public Customer(Integer id,
                    String name,
                    String password,
                    Date date,
                    String gender,
                    String profileImageId) {
        this(id,name,password,date,gender);
        this.profileImageId = profileImageId;
    }
    public Customer(String name,
                    String password,
                    Date date,
                    String gender) {
        this.name = name;
        this.password = password;
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

    public String getProfileImageId() {
        return profileImageId;
    }

    public void setProfileImageId(String profileImageId) {
        this.profileImageId = profileImageId;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(getId(), customer.getId()) && Objects.equals(getName(), customer.getName()) && Objects.equals(getDate(), customer.getDate()) && Objects.equals(getGender(), customer.getGender()) && Objects.equals(getPassword(), customer.getPassword()) && Objects.equals(getProfileImageId(), customer.getProfileImageId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDate(), getGender(), getPassword(), getProfileImageId());
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", profileImageId='" + profileImageId + '\'' +
                '}';
    }
}
