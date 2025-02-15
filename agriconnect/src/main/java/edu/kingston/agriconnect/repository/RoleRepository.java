package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.Role;
import edu.kingston.agriconnect.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(RoleName roleName);

    @Query(nativeQuery = true,

            value = "select " +
                    "        r1_0.role_id," +
                    "        r1_0.role_name," +

                    "        u1_1.id," +
                    "        u1_1.user_type," +
                    "        u1_1.address," +
                    "        u1_1.email," +
                    "        u1_1.name," +
                    "        u1_1.password," +
                    "        u1_1.phone," +
                    "        u1_1.status " +
                    "    from " +
                    "        roles r1_0 " +
                    "    join " +
                    "        user_roles u1_0 " +
                    "            on r1_0.role_id=u1_0.role_id " +
                    "    join " +
                    "        users u1_1 " +
                    "            on u1_1.id=u1_0.user_id " +
                    "    where r1_0.role_name= :roleName "
    )
    Optional<Role> findByRoleNameEager(@Param("roleName") String roleName);
}
