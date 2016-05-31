package com.bra.modules.cms.entity;

import com.bra.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 战队Entity
 * @author ddt
 * @version 2016-05-28
 */
public class Team extends DataEntity<Team> {
	
	private static final long serialVersionUID = 1L;
	private String photo;		// photo
	private String name;		// name
	private String personNum;		// person_num
	private String position;		// position
	private String positionX;		// position_x
	private String positionY;		// position_y
	private TeamMember captain;		// captain
	private Category group;
	private List<TeamMember> members;
	
	public Team() {
		super();
	}

	public Team(String id){
		super(id);
	}

	@Length(min=0, max=255, message="photo长度必须介于 0 和 255 之间")
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	@Length(min=0, max=255, message="name长度必须介于 0 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=11, message="person_num长度必须介于 0 和 11 之间")
	public String getPersonNum() {
		return personNum;
	}

	public void setPersonNum(String personNum) {
		this.personNum = personNum;
	}
	
	@Length(min=0, max=255, message="position长度必须介于 0 和 255 之间")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
	@Length(min=0, max=255, message="position_x长度必须介于 0 和 255 之间")
	public String getPositionX() {
		return positionX;
	}

	public void setPositionX(String positionX) {
		this.positionX = positionX;
	}
	
	@Length(min=0, max=255, message="position_y长度必须介于 0 和 255 之间")
	public String getPositionY() {
		return positionY;
	}

	public void setPositionY(String positionY) {
		this.positionY = positionY;
	}


	public TeamMember getCaptain() {
		return captain;
	}

	public void setCaptain(TeamMember captain) {
		this.captain = captain;
	}


	public List<TeamMember> getMembers() {
		return members;
	}

	public void setMembers(List<TeamMember> members) {
		this.members = members;
	}


	public Category getGroup() {
		return group;
	}

	public void setGroup(Category group) {
		this.group = group;
	}





}