/**
 * 
 */
package com.ipcs.service;

import java.util.List;

/**
 * @author Chen Chao
 *
 */
public interface AdminService<T> {
	public void addBatchSubodinates(List<T> subodidates);
	
	public void deleteBatchSubodinates(List<T> subodinates);
	
	public void addAdmin(T admin);
	
	public List<T> listAllStudents(String schoolName);
	
	public List<T> listAllTeachers(String schoolName);
	
	public boolean updateChild(T person);
	
	public boolean updateTeacher(T teacher);
	
	public boolean broadcaseMessageTo(List<T> subodidates);
	
	
	

}
