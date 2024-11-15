package org.example.springboot_demo.configurations;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.springboot_demo.entities.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Transactional
public class UserDetailsImpl implements UserDetails {

    private final EmployeeEntity employeeEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(employeeEntity.getRole().getRoleName()));
        return roles;
    }

    public EmployeeEntity getEmployee(){
        return this.employeeEntity;
    }

    @Override
    public String getPassword() {
        if(employeeEntity == null)
            return null;
        if(employeeEntity.getPassword() == null)
            return  null;
        return employeeEntity.getPassword();
    }

    @Override
    public String getUsername() {
        if(employeeEntity == null)
            return null;
        if(employeeEntity.getUsername() == null)
            return  null;
        return employeeEntity.getUsername();
    }

    public String getRoleName() {
        return employeeEntity.getRole().getRoleName();
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
}
