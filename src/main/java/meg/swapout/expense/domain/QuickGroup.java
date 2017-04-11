package meg.swapout.expense.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class QuickGroup {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;
	
	@Size(max = 100)
	private String name;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "quickgroup", fetch=FetchType.LAZY)
	private List<QuickGroupDetail> groupdetails;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<QuickGroupDetail> getGroupdetails() {
		return groupdetails;
	}

	public void setGroupdetails(List<QuickGroupDetail> groupdetails) {
		this.groupdetails = groupdetails;
	}
	

}
