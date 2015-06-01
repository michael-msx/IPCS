/**
 *
 */
package com.ipcs.service.impl;

import com.ipcs.dao.*;
import com.ipcs.model.*;
import com.ipcs.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chen Chao
 */

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private PersonDao personDao;

    @Autowired
    private RelationshipTypeDao relationshipTypeDao;

    @Autowired
    private ActivityDao activityDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private MessageDao MessageDao;

    @Autowired
    private SchoolDao schoolDao;

    @Autowired
    private PersonDetailDao personDetailDao;

    @Autowired
    private ActivitytTypeDao activityTypeDao;

    @Transactional
    public void addBatchSubodinates(List<Person> subodidates) {
        personDao.batchSave(subodidates);
    }

    @Transactional
    public void addPerson(Person person) {
        if(null != person.getSchool())
            person.setSchool(getSchoolByName(person.getSchool().getName()));

        List<Role> roles = new ArrayList<Role>();
        roles.addAll(person.getRoles());
        person.evictRoles();
        for (Role role : roles) {
            role = getRoleByName(role.getName());
            if (null != role)
                person.addRole(role);
        }

        List<Contact> contacts = person.getContacts();
        for (Contact contact : contacts) {
            RelationshipType type = getRelationshipTypeByName(contact.getRelationshipType().getName());
            contact.setRelationshipType(type);
            contact.setPerson(person);
        }

        if(person.getPersonDetail()!=null)
            personDetailDao.save(person.getPersonDetail());
        personDao.save(person);
    }

    @Transactional
    public void removePerson(Person person) {
        personDao.delete(person);
    }

    @Transactional(readOnly=true)
    public List<Person> listAllPersonByRoleName(String schoolName, String roleName) {
        return personDao.listPersonsBy(schoolName,roleName);
    }


    @Transactional
    public void updatePerson(Person person) {
        Person persistPerson = personDao.load(person.getObjectId());
        persistPerson.setAccount_name(person.getAccount_name());
        persistPerson.setPassword_hash(person.getPassword_hash());
        PersonDetail personDetail = persistPerson.getPersonDetail();
        personDetail.setFirstName(person.getPersonDetail().getFirstName());
        personDetail.setLastName(person.getPersonDetail().getLastName());
        personDetail.setAge(person.getPersonDetail().getAge());
        personDetail.setDateOfBirth(person.getPersonDetail().getDateOfBirth());
        personDetail.setNationality(person.getPersonDetail().getNationality());
        personDetail.setNric(person.getPersonDetail().getNric());
        personDetailDao.update(personDetail);


        List<Contact> persistContacts =  persistPerson.getContacts();
        for(Contact persistContact:persistContacts ){
            for(Contact transientContact: person.getContacts()){
                if(persistContact.getObjectId() == transientContact.getObjectId())
                {
                    persistContact.setMobileNumber(transientContact.getMobileNumber());
                    persistContact.setContacterName(transientContact.getContacterName());
                    persistContact.setAddress(transientContact.getAddress());
                    persistContact.getRelationshipType().setName(transientContact.getRelationshipType().getName());
                }
            }
        }
        persistPerson.setPersonDetail(personDetail);
        personDao.update(persistPerson);
    }

    @Transactional
    public boolean broadcaseMessageTo(List<Person> subodidates) {
        return false;
    }


    @Transactional
    public void deleteBatchSubodinates(List<Person> subodinates) {
        personDao.deleteAll(subodinates);
    }

    @Transactional(readOnly=true)
    public Role getRoleByName(String name) {
        return roleDao.findRoleByName(name);
    }

    @Transactional(readOnly=true)
    public RelationshipType getRelationshipTypeByName(String name) {
        return relationshipTypeDao.findRelationshipTypeByName(name);
    }


    @Transactional(readOnly=true)
    public School getSchoolByName(String name) {
        return schoolDao.findSchoolByName(name);
    }

    @Transactional(readOnly=true)
    public List<School> getSchoolByType(String typeName) {
        return schoolDao.listSchoolsByType(typeName);
    }

    @Transactional(readOnly=true)
    public Person getPersonInfo(String name) {
        return personDao.findPersonDetailsByName(name);
    }

    @Transactional(readOnly=true)
    public List<Message> listAllMessages(String adminName) {
        return MessageDao.find("select m from Message m inner join m.fromUser p where p.account_name = '" + adminName + "'");
    }

    @Transactional(readOnly=true)
    public List<Activity> listAllActivities(Long childId) {
        return activityDao.listActivitieByChild(childId);
    }

    @Transactional(readOnly=true)
    public List<Person> listAllChild(Long parentId) {
        return personDao.find("select w from Relationship r inner join r.whose w inner join r.type t inner join r.iswho i where t.name = 'PARENT' and i.objectId = '" + parentId + "'");
    }

    @Transactional(readOnly=true)
    public List<Activity> listAllActivitiesFromAdmin(String adminName){
        return activityDao.listActivitiesByAdmin(adminName);
    }

    @Transactional(readOnly=true)
    public Person findPersonByName(String accountName){
        return personDao.findPersonByName(accountName);
    }

    @Transactional
    public void addActivity(Activity activity){
        List<Person> trainsientPersons = new ArrayList<Person>();
        List<Person> students = activity.getPersons();
        if(null != students && students.size()!=0){
            trainsientPersons.addAll(students);
            activity.getPersons().clear();;
            for (Person person : trainsientPersons) {
                person = findPersonByName(person.getAccount_name());
                if (null != person)
                    activity.addPerson(person);
            }
        }
        ActivityType activityType = activityTypeDao.findActivityByName(activity.getActivityType().getName());
        activity.setActivityType(activityType);
        Person host = findPersonByName(activity.getHost().getAccount_name());
        activity.setHost(host);
        School school = getSchoolByName(activity.getSchool().getName());
        activity.setSchool(school);
        activityDao.save(activity);
    }

    @Transactional
    public void updateActivity(Activity activity){        {
        Activity persistedActivity = activityDao.load(activity.getObjectId());
        persistedActivity.setName(activity.getName());
        persistedActivity.setDescription(activity.getDescription());
        persistedActivity.setLocation(activity.getLocation());
        persistedActivity.setStartTime(activity.getStartTime());

        Person persistedHost = findPersonByName(activity.getHost().getAccount_name());
        persistedActivity.setHost(persistedHost);

        List<Person> trainsientPersons = new ArrayList<Person>();
        List<Person> students = activity.getPersons();
        if(null!=students&&students.size() != 0){
            trainsientPersons.addAll(students);
            persistedActivity.getPersons().clear();
            for (Person person : trainsientPersons) {
                person = findPersonByName(person.getAccount_name());
                if (null != person)
                    persistedActivity.addPerson(person);
            }
        }

        ActivityType activityType = activityTypeDao.findActivityByName(activity.getActivityType().getName());
        persistedActivity.setActivityType(activityType);
        activityDao.update(persistedActivity);
        }
    }


    @Transactional
    public void deleteActivity(Long activtyId){
        Activity activity = activityDao.get(activtyId);
        activityDao.delete(activity);
    }

    @Transactional
    public void deletePerson(Long personId){
        Person person = personDao.get(personId);
        personDao.delete(person);
    }

    @Transactional(readOnly=true)
    public Activity getActivityDetail(Long activityId){
        return activityDao.findActivityDetailsById(activityId);
    }

    @Transactional(readOnly= true)
    public List<ActivityType> listAllActivityType(){
        return activityTypeDao.findAll();
    }

    @Transactional(readOnly= true)
    public List<Activity> listActivityByType(String typeName){
        return activityDao.listActivitiesByType(typeName);
    }
}
