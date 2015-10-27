package cz.muni.fi.pa165.dao.jpa;

import cz.muni.fi.pa165.dao.CustomerDao;
import cz.muni.fi.pa165.dao.DAOBase;
import cz.muni.fi.pa165.entity.Customer;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;

/**
 *
 * @author Jana Cechackova
 */
@Repository
public class CustomerDaoImpl extends DAOBase implements CustomerDao {

    @Override
    public void addCustomer(Customer customer) {
	if (customer == null){
	    throw new IllegalArgumentException("Customer is null.");
	}
	try {
	    getEntityManager().persist(customer);
	}
	catch (Exception ex){
	    throw new PersistenceException("Transaction failed.\n"+ ex.getMessage(), ex);
	}
    }

    @Override
    public void updateCustomer(Customer customer) {
	if (customer == null){
	    throw new IllegalArgumentException("Customer is null.");
	}
	try {
	    getEntityManager().merge(customer);
	}
	catch (Exception ex){
	    throw new PersistenceException("Transaction failed.\n" + ex.getMessage(), ex);
	}
    }

    @Override
    public void deleteCustomer(Customer customer){
	try {
            Customer toBeRemoved = getEntityManager().merge(customer);
            if (toBeRemoved != null) {
                getEntityManager().remove(toBeRemoved);
            }
        }
        catch(Exception ex){
            throw new PersistenceException("Transaction failed.\n" + ex.getMessage(), ex);
        }
    }

    @Override
    public Customer getCustomerById(Long Id){
	return getEntityManager().find(Customer.class, Id);
    }
}
