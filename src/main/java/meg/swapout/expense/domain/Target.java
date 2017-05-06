package meg.swapout.expense.domain;

import meg.swapout.expense.services.TargetType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name  = "TARGETGROUP")
public class Target {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	private Long targettype;

	@Enumerated(EnumType.STRING)
	@NotNull
	private TargetType type;

	@NotNull
	@Size(max = 60)
	private String name;

	@Size(max = 200)
	private String description;

	private Boolean isdefault;

	private String monthtag;

	private String yeartag;

	private String tag;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "targetgroup")
	private List<TargetDetail> targetdetails = new ArrayList<>();

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

	public Long getTargettype() {
		return targettype;
	}

	public void setTargettype(Long targettype) {
		this.targettype = targettype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsdefault() {
		return isdefault;
	}

	public void setIsdefault(Boolean isdefault) {
		this.isdefault = isdefault;
	}

	public String getMonthtag() {
		return monthtag;
	}

	public void setMonthtag(String monthtag) {
		this.monthtag = monthtag;
	}

	public String getYeartag() {
		return yeartag;
	}

	public void setYeartag(String yeartag) {
		this.yeartag = yeartag;
	}

	public List<TargetDetail> getTargetdetails() {
		return targetdetails;
	}

	public void setTargetdetails(List<TargetDetail> targetdetails) {
		this.targetdetails = targetdetails;
	}

	public TargetType getType() {
		return type;
	}

	public void setType(TargetType type) {
		this.type = type;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
