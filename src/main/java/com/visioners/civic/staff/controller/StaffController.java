// package com.visioners.civic.staff.controller;

// import com.visioners.civic.staff.entity.Staff;
// import com.visioners.civic.staff.repository.StaffRepository;
// import com.visioners.civic.user.entity.Users;
// import com.visioners.civic.user.repository.UsersRepository;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/staff")
// @CrossOrigin(origins = "*")
// public class StaffController {

//     private final StaffRepository staffRepository;
//     private final UsersRepository usersRepository;

//     public StaffController(StaffRepository staffRepository, UsersRepository usersRepository) {
//         this.staffRepository = staffRepository;
//         this.usersRepository = usersRepository;
//     }

//     @GetMapping("/field-workers")
//     public ResponseEntity<List<Staff>> getFieldWorkers(Authentication authentication) {
//         // 1. Get logged-in username (or mobile)
//         String username = authentication.getName();

//         // 2. Find user
//         Users loggedInUser = usersRepository.findByMobileNumberWithRoles(username)  // change to findByMobileNumber if needed
//                 .orElseThrow(() -> new RuntimeException("User not found: " + username));

//         // 3. Find staff record for this user
//         Staff staff = staffRepository.findByUserId(loggedInUser.getId())
//                 .orElseThrow(() -> new RuntimeException("Staff not found for user: " + username));

//         // 4. Department of the staff
//         Long departmentId = staff.getDepartment().getId();

//         // 5. Fetch all staff in this department
//         List<Staff> allDepartmentStaff = staffRepository.findByDepartmentId(departmentId);

//         // 6. Filter only STAFF_FILDER role
//         List<Staff> fieldWorkers = allDepartmentStaff.stream()
//                 .filter(s -> s.getUser().getRoles().stream()
//                         .anyMatch(r -> "STAFF_FILDER".equals(r.getName())))
//                 .toList();

//         return ResponseEntity.ok(fieldWorkers);
//     }
// }

package com.visioners.civic.staff.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.visioners.civic.issue.dto.FieldWorkersDTO;
import com.visioners.civic.staff.entity.Staff;
import com.visioners.civic.staff.repository.StaffRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StaffController {
        private final StaffRepository staffRepository;

        @GetMapping("/staff/{deptId}/fieldWorkers")
        public ResponseEntity<List<FieldWorkersDTO>> getFieldWorkers(@PathVariable("deptId") Long departmentId) {
                List<Staff> staffs = staffRepository.findByDepartmentId(departmentId);
                return ResponseEntity.ok(
                        staffs
                                .stream()
                                .map(s -> new FieldWorkersDTO(s.getUser().getId(), s.getUser().getUsername()))
                                .collect(Collectors.toList())
                );
        }
}