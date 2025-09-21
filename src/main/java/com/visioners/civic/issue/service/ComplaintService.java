import org.springframework.stereotype.Service;

import com.visioners.civic.issue.entity.Complaint;
import com.visioners.civic.issue.model.IssueStatus;
import com.visioners.civic.user.entity.Users;
import com.visioners.civic.user.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepo;
    private final UsersRepository usersRepo;
    private final DistrictRepository districtRepo;
    private final BlockRepository blockRepo;
    private final DepartmentRepository deptRepo;

    public Complaint submitComplaint(ComplaintRequest request, String mobileNumber) {

        Users citizen = usersRepo.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // validate category-subcategory
        IssueValidator.validateCategorySubcategory(request.category(), request.subCategory());

        Complaint complaint = new Complaint();
        complaint.setDescription(request.description());
        complaint.setCategory(request.category());
        complaint.setSubCategory(request.subCategory());
        complaint.setSeverity(request.severity());
        complaint.setLocation(request.location());
        complaint.setImageUrl(request.imageUrl());
        complaint.setStatus(IssueStatus.OPEN);
        complaint.setRaisedBy(citizen);

        // resolve district, block, department from location
        complaint.setDistrict(districtRepo.findByName(request.location().getAdminArea()));
        complaint.setBlock(blockRepo.findByName(request.location().getSubAdminArea()));
        // for MVP, assign department based on category
        complaint.setDepartment(deptRepo.findByCategory(request.category()));

        return complaintRepo.save(complaint);
    }
}

