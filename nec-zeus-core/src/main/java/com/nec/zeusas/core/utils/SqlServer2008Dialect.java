package com.nec.zeusas.core.utils;

import java.sql.Types;

import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.type.StandardBasicTypes;

public final class SqlServer2008Dialect extends SQLServerDialect {  
	  
    public SqlServer2008Dialect() {  
        super();  
        registerHibernateType(1, "string");  
        registerHibernateType(-9, "string");  
        registerHibernateType(-16, "string");  
        registerHibernateType(3, "double");  
        
        registerHibernateType(Types.CHAR, StandardBasicTypes.STRING.getName());     
        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());     
        registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.STRING.getName());     
        registerHibernateType(Types.DECIMAL, StandardBasicTypes.DOUBLE.getName());
    }  
}  