package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.ExpenseDao;
import meg.swapout.expense.repositories.RawTransactionRepository;
import meg.swapout.expense.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CategoryService catService;

    @Autowired
    private RawTransactionRepository bankTransRep;

    /* (non-Javadoc)
     * @see meg.bank.bus.SearchService#getExpenses(meg.bank.bus.ExpenseCriteria)
     */
    @Override
    public List<ExpenseDao> getExpenses(ExpenseCriteria criteria) {
        if (criteria == null) {
            criteria = new ExpenseCriteria();
        }

        List<ExpenseDao> expenses;
        // save orig cat type
        CategorizedType origcattype = criteria.getCategorizedType();

        // check if category list has been filled, if necessary
        if (criteria.getShowSubcats() != null && criteria.getShowSubcats().booleanValue()) {
            // want to show subcategories, but they haven't been filled in - fix this
            Long categoryId = criteria.getCategory() != null ? criteria.getCategory().getId() : null;
            if (categoryId != null) {
                CategoryLevel catlevel = catService.getAsCategoryLevel(categoryId);
                Category cat = catlevel.getCategory();
                List<CategoryLevel> subcats = catService.getAllSubcategories(cat);
                subcats.add(catlevel);
                criteria.setCategoryLevelList(subcats);
            }
        }

        if (origcattype == null || origcattype == CategorizedType.All) {
            // all
            expenses = getExpenseByCatType(criteria, CategorizedType.NonCategorized);
            List<ExpenseDao> expensescats = getExpenseByCatType(criteria, CategorizedType.OnlyCategorized);
            expenses.addAll(expensescats);
            criteria.setCategorizedType(origcattype);
        } else {
            expenses = getExpenseByCatType(criteria, origcattype);
        }


        return expenses;
    }

    /* (non-Javadoc)
     * @see meg.bank.bus.SearchService#getExpenseTotalByMonth(meg.bank.bus.ExpenseCriteria)
     */
    @Override
    public List<CategorySummary> getExpenseTotalByMonthAndCategory(ExpenseCriteria criteria) {
        List<CategorySummary> displays = new ArrayList<CategorySummary>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CategorySummary> c = cb.createQuery(CategorySummary.class);
        Root<ExpenseDao> exp = c.from(ExpenseDao.class);
        Expression maxExpression = cb.sum(exp.<Number>get("catamount"));
        c.multiselect(exp.get("monthyear"), exp.get("catName"), maxExpression.alias("sum"))
                .groupBy(exp.get("monthyear"), exp.get("catName"))
                .having(cb.isNotNull(maxExpression));

        if (criteria != null) {

            // get where clause
            List<Predicate> whereclause = getPredicatesForCriteria(criteria, cb, exp);

            // creating the query
            c.where(cb.and(whereclause.toArray(new Predicate[whereclause.size()])));
            TypedQuery<CategorySummary> q = entityManager.createQuery(c);

            // setting the parameters
            setParametersInQuery(criteria, q);

            return q.getResultList();

        }

        return null;
/*
 * 
 * 		
		// create sql
		String summedcol = "catamount";
		StringBuffer sql = new StringBuffer("select month,year, sum(").append(
				summedcol).append(") from expense ");
		sql.append(getWhereClauseForCriteria(criteria, false));
		sql.append("group by month,year");

		// execute sql, and retrieve results
		List results = executeAggregateQuery(sql.toString());

		// populate CategorySummary objects from results
		if (results != null) {
			for (Iterator iter = results.iterator(); iter.hasNext();) {
				Object[] row = (Object[]) iter.next();
				CategorySummary catsum = new CategorySummary();
				Integer month = (Integer) row[0];
				Integer year = (Integer) row[1];
				Double total = (Double) row[2];
				Calendar cal = Calendar.getInstance();
				cal.set(year.intValue(), month.intValue() - 1, 1);
				catsum.setSummaryDate(cal.getTime());
				catsum.setSum(total.doubleValue());
				displays.add(catsum);
			}
		}

		// return list of CategorySummary objects
		return displays;
 */
    }


    @Override
    public List<CategorySummary> getExpenseTotalByMonth(ExpenseCriteria criteria) {
        List<CategorySummary> displays = new ArrayList<CategorySummary>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CategorySummary> c = cb.createQuery(CategorySummary.class);
        Root<ExpenseDao> exp = c.from(ExpenseDao.class);
        Expression maxExpression = cb.sum(exp.<Number>get("catamount"));
        c.multiselect(exp.get("monthyear"), maxExpression.alias("sum"))
                .groupBy(exp.get("monthyear"));

        if (criteria != null) {

            // get where clause
            List<Predicate> whereclause = getPredicatesForCriteria(criteria, cb, exp);

            // creating the query
            c.where(cb.and(whereclause.toArray(new Predicate[whereclause.size()])));
            TypedQuery<CategorySummary> q = entityManager.createQuery(c);
            q.setFlushMode(FlushModeType.COMMIT);

            // setting the parameters
            setParametersInQuery(criteria, q);

            return q.getResultList();

        }

        return null;

    }

    /* (non-Javadoc)
     * @see meg.bank.bus.SearchService#getExpenseTotalByYear(meg.bank.bus.ExpenseCriteria)
     */
    @Override
    public List<CategorySummary> getExpenseTotalByYear(ExpenseCriteria criteria) {

        List<CategorySummary> displays = new ArrayList<CategorySummary>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CategorySummary> c = cb.createQuery(CategorySummary.class);
        Root<ExpenseDao> exp = c.from(ExpenseDao.class);
        Expression maxExpression = cb.sum(exp.<Number>get("catamount"));
        c.multiselect(exp.get("year"), maxExpression.alias("sum"))
                .groupBy(exp.get("year"));

        if (criteria != null) {

            // get where clause
            List<Predicate> whereclause = getPredicatesForCriteria(criteria, cb, exp);

            // creating the query
            c.where(cb.and(whereclause.toArray(new Predicate[whereclause.size()])));
            TypedQuery<CategorySummary> q = entityManager.createQuery(c);

            // setting the parameters
            setParametersInQuery(criteria, q);

            return q.getResultList();

        }

        return null;
    }

    public List<CategorySummary> getExpenseTotal(
            ExpenseCriteria criteria) {
        List<CategorySummary> displays = new ArrayList<CategorySummary>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CategorySummary> c = cb.createQuery(CategorySummary.class);
        Root<ExpenseDao> exp = c.from(ExpenseDao.class);
        Expression maxExpression = cb.sum(exp.<Double>get("catamount"));
        c.multiselect(maxExpression.alias("sum"));

        if (criteria != null) {

            // get where clause
            List<Predicate> whereclause = getPredicatesForCriteria(criteria, cb, exp);

            // creating the query
            c.where(cb.and(whereclause.toArray(new Predicate[whereclause.size()])));
            TypedQuery<CategorySummary> q = entityManager.createQuery(c);

            // setting the parameters
            setParametersInQuery(criteria, q);

            return q.getResultList();

        }

        return null;
    }

    @Override
    public CategorySummary getCategorySummaryByMonthYear(ExpenseCriteria criteria) {
        List<CategorySummary> displays = new ArrayList<CategorySummary>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CategorySummary> c = cb.createQuery(CategorySummary.class);
        Root<ExpenseDao> exp = c.from(ExpenseDao.class);
        Expression maxExpression = cb.sum(exp.<Number>get("catamount"));
        c.multiselect(exp.get("monthyear"), maxExpression.alias("sum"))
                .groupBy(exp.get("monthyear"));

        if (criteria != null) {

            // get where clause
            List<Predicate> whereclause = getPredicatesForCriteria(criteria, cb, exp);

            // creating the query
            c.where(cb.and(whereclause.toArray(new Predicate[whereclause.size()])));
            TypedQuery<CategorySummary> q = entityManager.createQuery(c);

            // setting the parameters
            setParametersInQuery(criteria, q);

            List<CategorySummary> summaryList = q.getResultList();
            if (!summaryList.isEmpty() && summaryList.size() > 0) {
                return summaryList.get(0);
            }
        }

        return null;
    }

    public List<ExpenseDao> getExpenseListByIds(List<String> idlist) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExpenseDao> c = cb.createQuery(ExpenseDao.class);
        Root<ExpenseDao> exp = c.from(ExpenseDao.class);
        c.select(exp);

        if (idlist != null) {
            // making space for parameters
            ParameterExpression<Collection> ids = cb.parameter(Collection.class);
            c.where(exp.get("id").in(ids));
            Collection<String> idsParameter = new ArrayList<String>();
            for (String param : idlist) {
                idsParameter.add(param);
            }

            // creating the query
            TypedQuery<ExpenseDao> q = entityManager.createQuery(c);

            // setting the parameters
            q.setParameter(ids, idsParameter);


            return q.getResultList();

        }

        return null;
    }


    @Override
    public List<ExpenseDao> getAllExpenses() {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExpenseDao> c = cb.createQuery(ExpenseDao.class);
        Root<ExpenseDao> exp = c.from(ExpenseDao.class);
        c.select(exp);
        c.orderBy(cb.desc(exp.get("transdate")), cb.asc(exp.get("transid")));

        // creating the query
        TypedQuery<ExpenseDao> q = entityManager.createQuery(c);

        return q.getResultList();

    }

    /* (non-Javadoc)
     * @see meg.bank.bus.BankTransactionService#getMostRecentTransDate()
     */
    @Override
    public Date getMostRecentTransDate() {
        // call banktransservice.getMostRecentTransDate();
        Date resultdate = bankTransRep.getMostRecentTransDate();

        // check for null
        if (resultdate == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(1970, 10, 1);
            resultdate = cal.getTime();
        }

        return resultdate;
    }


    @Override
    public Date getFirstTransDate() {
        return bankTransRep.getFirstTransDate();
    }

    private List<ExpenseDao> getExpenseByCatType(ExpenseCriteria criteria,
                                                 CategorizedType catType) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExpenseDao> c = cb.createQuery(ExpenseDao.class);
        Root<ExpenseDao> exp = c.from(ExpenseDao.class);
        c.select(exp);
        setOrderBy(criteria, cb, c, exp);


        if (criteria != null) {

            // set the cattype in the criteria
            criteria.setCategorizedType(catType);
            // get where clause
            List<Predicate> whereclause = getPredicatesForCriteria(criteria, cb, exp);


            // creating the query
            c.where(cb.and(whereclause.toArray(new Predicate[whereclause.size()])));
            TypedQuery<ExpenseDao> q = entityManager.createQuery(c);

            // setting the parameters
            setParametersInQuery(criteria, q);

            return q.getResultList();

        }

        return null;
    }

    private void setParametersInQuery(ExpenseCriteria criteria,
                                      TypedQuery q) {
        // date start
        if (criteria.getDateStart() != null) {
            q.setParameter("transdate", criteria.getDateStart());
        }
        if (criteria.getDateEnd() != null) {
            q.setParameter("transdateend", criteria.getDateEnd());
        }
        // categorized type
        if (criteria.getCategorizedType() != null && criteria.getCategorizedType() != CategorizedType.All) {
            if (criteria.getCategorizedType() == CategorizedType.NonCategorized) {
                q.setParameter("hascat", false);
            } else if (criteria.getCategorizedType() == CategorizedType.OnlyCategorized) {
                q.setParameter("hascat", true);
            }
        }
        // set category or categories
        if (criteria.showSingleCategory()) {
            q.setParameter("catid", criteria.getCategory().getId());
        }
        // non-expense handling
        if (criteria.getExcludeNonExpense() != null) {
            if (criteria.getExcludeNonExpense().booleanValue()) {
                q.setParameter("nonexpense", false);
            }
        }
        // transactiontype
        if (criteria.getTransactionType() != null) {
            if (criteria.getTransactionType() ==
                    TransactionType.Credits) {
                q.setParameter("transtotal", new Double(0));
            } else if (criteria.getTransactionType() == TransactionType.Debits) {
                q.setParameter("transtotal", new Double(0));
            }
        }

    }

    private List<Predicate> getPredicatesForCriteria(ExpenseCriteria criteria, CriteriaBuilder cb, Root<ExpenseDao> exp) {
        // put together where clause
        List<Predicate> whereclause = new ArrayList<Predicate>();


        // making space for parameters
        // date start
        if (criteria.getDateStart() != null) {
            ParameterExpression<Date> param = cb.parameter(Date.class,
                    "transdate");
            whereclause.add(cb.greaterThanOrEqualTo(
                    exp.<Date>get("transdate"), param));

        }
        if (criteria.getDateEnd() != null) {
            ParameterExpression<Date> param = cb.parameter(Date.class,
                    "transdateend");
            whereclause.add(cb.lessThan(
                    exp.<Date>get("transdate"), param));

        }
        // categorized type
        if (criteria.getCategorizedType() != null && criteria.getCategorizedType() != CategorizedType.All) {
            // categorized type exists - add to sql
            ParameterExpression<Boolean> param = cb.parameter(Boolean.class,
                    "hascat");
            whereclause.add(cb.equal(exp.get("hascat"), param));
        }
        // set category or categories
        if (criteria.showSingleCategory()) {
            ParameterExpression<Long> param = cb.parameter(Long.class,
                    "catid");
            whereclause.add(cb.equal(exp.get("catid"), param));
        } else if (criteria.showListOfCategories()) {
            List<Long> catids = new ArrayList<Long>();
            for (CategoryLevel catlvl : criteria.getCategoryLevelList()) {
                Category cat = catlvl.getCategory();
                catids.add(cat.getId());
            }

            Expression<Long> param = exp.<Long>get("catid");
            whereclause.add(exp.get("catid").in(catids));
        }
        // non-expense handling
        if (criteria.getExcludeNonExpense() != null) {
            if (criteria.getExcludeNonExpense().booleanValue()) {
                ParameterExpression<Boolean> param = cb.parameter(Boolean.class,
                        "nonexpense");
                whereclause.add(cb.equal(exp.get("nonexpense"), param));
            }
        }
        // transactiontype
        if (criteria.getTransactionType() != null) {
            if (criteria.getTransactionType() == TransactionType.Credits) {
                ParameterExpression<Double> param = cb.parameter(Double.class,
                        "transtotal");
                whereclause.add(cb.gt(exp.<Double>get("transtotal"), param));
            } else if (criteria.getTransactionType() ==
                    TransactionType.Debits) {
                ParameterExpression<Double> param = cb.parameter(Double.class,
                        "transtotal");
                whereclause.add(cb.lt(exp.<Double>get("transtotal"), param));
            }
        }
        // candidates for rounding
        if (criteria.getForRounding()) {
            // has category - already for categorized type
            // not already rounded
            whereclause.add(cb.isFalse(exp.get("rounded")));
            // category amount same as transaction amount
            whereclause.add(cb.equal(exp.get("transtotal"), exp.get("catamount")));
        }
        // candidates for rounding
        if (criteria.getRoundedOnly()) {
            // already rounded
            whereclause.add(cb.isTrue(exp.get("rounded")));
        }
        // search text
        if (criteria.getSearchText() != null && criteria.getSearchText().trim()
                .length() > 0) {
            whereclause.add(cb.like(cb.lower(exp.get("description")), "%" + criteria.getSearchText().toLowerCase() + "%"));
        }

        // always exclude deleted
        whereclause.add(cb.isFalse(exp.get("deleted")));

        return whereclause;
    }

    private void setOrderBy(ExpenseCriteria criteria, CriteriaBuilder cb,
                            CriteriaQuery<ExpenseDao> query, Root<ExpenseDao> root) {

        List<String> sortstrings = new ArrayList<String>();
        // set sort string
        if (criteria.getSortfield() != null) {
            if (criteria.getSortfield().equals(ExpenseCriteria.SortType.Amount)) {
                sortstrings.add("displayamount");
            } else if (criteria.getSortfield().equals(
                    ExpenseCriteria.SortType.Category)) {
                sortstrings.add("catName");
            } else if (criteria.getSortfield().equals(
                    ExpenseCriteria.SortType.Date)) {
                sortstrings.add("transdate");
                sortstrings.add("transid");
            } else if (criteria.getSortfield().equals(
                    ExpenseCriteria.SortType.Detail)) {
                sortstrings.add("detail");
            }
        } else {
            sortstrings.add("transdate");
            sortstrings.add("transid");
        }

        // now, put into list of order objects with direction
        List<Order> orderstatements = new ArrayList<Order>();
        for (String sortparam : sortstrings) {
            if (criteria.getSortdir() != null && criteria.getSortdir().longValue() == ExpenseCriteria.SortDirection.Asc) {
                Order neworder = cb.asc(root.get(sortparam));
                orderstatements.add(neworder);
            } else {
                Order neworder = cb.desc(root.get(sortparam));
                orderstatements.add(neworder);
            }
        }
        query.orderBy(orderstatements);

    }

    @Override
    public List<CategorySummary> getExpenseTotalByYearAndCategory(
            ExpenseCriteria criteria) {

        List<CategorySummary> displays = new ArrayList<CategorySummary>();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CategorySummary> c = cb.createQuery(CategorySummary.class);
        Root<ExpenseDao> exp = c.from(ExpenseDao.class);
        Expression maxExpression = cb.sum(exp.<Number>get("catamount"));
        c.multiselect(exp.get("year"), exp.get("catName"), maxExpression.alias("sum"))
                .groupBy(exp.get("year"), exp.get("catName"));

        if (criteria != null) {

            // get where clause
            List<Predicate> whereclause = getPredicatesForCriteria(criteria, cb, exp);

            // creating the query
            c.where(cb.and(whereclause.toArray(new Predicate[whereclause.size()])));
            TypedQuery<CategorySummary> q = entityManager.createQuery(c);

            // setting the parameters
            setParametersInQuery(criteria, q);

            return q.getResultList();

        }

        return null;

    }


}	

