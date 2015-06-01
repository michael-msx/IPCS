package com.ipcs.dao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.ipcs.model.Role;

import java.util.List;

/**
 * @author Chen Chao
 *
 */
@Repository
public class RoleDao extends GenericHibernateDao<Role, Long> {
    public Role findRoleByName(String name) {
        List<Role> roles =  find("from Role as r where  r.name = '" + name + "'");
        if(roles.size()==0)
            return null;
        return roles.get(0);
    }

}
