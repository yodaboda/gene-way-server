package com.nutrinfomics.geneway.server.data;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.customer.PersonalDetails;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;

public interface UserMapper {

	@Select("SELECT * FROM customer.customer WHERE username = #{username}")
	Customer selectCustomer(String username);

	@Select("SELECT * FROM customer.customer WHERE id = #{id}")
	Customer findCustomer(long id);

	@Select("SELECT * FROM session.session WHERE customer_id = #{id}")
	Session findSession(long id);

	
	@Select("SELECT * FROM customer.customer c left outer join session.session s on c.id = s.customer_id WHERE s.sid = #{sid}")
	Customer selectCustomerForSession(String sid);

	@Update("UPDATE customer.customer SET hashed_password= #{password} WHERE id = #{id}")
	void updateCustomerHashedPassword(Customer customer);
	
	@Select("SELECT hashed_password FROM customer.customer WHERE username = #{username}")
	String selectCustomerHashedPassword(Customer customer);

	@Select("SELECT * FROM session.session WHERE customer_id = #{id}")
	Session selectCustomerSession(Customer customer);
	 
	@Select("SELECT * FROM session.device d WHERE d.customer_id = #{id}")
	Device selectCustomerDevice(Customer customer);

	@Update("UPDATE session.session SET sid= #{sid} WHERE customer_id = #{customer.id}")
	void updateSession(Session session);


	@Insert("INSERT INTO users(username, email, first_name, last_name, hashed_password, uuid) VALUES (#{username},#{email},#{firstName},"
	 		+ "#{lastName},#{hashedPassword},#{uuid})")
	@Options(useGeneratedKeys=true, keyProperty="id", keyColumn="ID")
	int insertUser(Customer customer);

	@Insert("")
	@Options(useGeneratedKeys=true, keyProperty="id", keyColumn="ID")
	void insertPersonalDetails(PersonalDetails personalDetails);

	@Insert("INSERT INTO session.session(sid,customer_id) VALUES (#{sid},#{customer.id})")
	void insertSession(Session session);

	@Insert("INSERT INTO session.device(uuid,customer_id) VALUES (#{uuid},#{customer.id})")
	void insertDevice(Device device);

	@Insert("")
	void insertCustomerAssociations(Customer customer);


}
