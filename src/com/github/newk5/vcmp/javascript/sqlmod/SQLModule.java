package com.github.newk5.vcmp.javascript.sqlmod;

import com.eclipsesource.v8.V8;
import static com.github.newk5.vcmp.javascript.plugin.internals.Runtime.console;
import com.github.newk5.vcmp.javascript.plugin.module.Module;
import com.github.newk5.vcmp.javascript.sqlmod.injectables.SQLWrapper;
import io.alicorn.v8.V8JavaAdapter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class SQLModule extends Plugin {

    public static ThreadPoolExecutor pool;
    private static V8 v8 = com.github.newk5.vcmp.javascript.plugin.internals.Runtime.v8;

    public SQLModule(PluginWrapper wrapper) {
        super(wrapper);
        this.pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    @Extension
    public static class SQL implements Module {

        @Override
        public void inject() {
            try {
                V8JavaAdapter.injectClass("_SQLStatement_", Statement.class, v8);
                V8JavaAdapter.injectClass("_PreparedStatement_", PreparedStatement.class, v8);
                V8JavaAdapter.injectClass("_ResultSet_", ResultSet.class, v8);
                V8JavaAdapter.injectClass("SQLDate", java.sql.Date.class, v8);
                v8.executeVoidScript("var SQLOptions  = { RETURN_GENERATED_KEYS: 1  }; ");
                V8JavaAdapter.injectObject("SQLWrapper", new SQLWrapper(), v8);
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                Class.forName("org.sqlite.JDBC");
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(SQLModule.class.getName()).log(Level.SEVERE, null, ex);
                console.error("Error while injecting SQL classes: "+ex.toString());
            }
        }

        @Override
        public String javascript() {
            InputStream in = SQLModule.class.getResourceAsStream("module.js");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String code = reader.lines().collect(Collectors.joining("\n"));

            return code;
        }

    }
}
