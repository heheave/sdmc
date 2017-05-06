package main;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import util.ParserUtil;
import v.Configure;
import v.V;

import java.awt.*;
import java.util.Map;

/**
 * Created by xiaoke on 17-5-6.
 */
public class Main {

    private static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        StringBuffer paras = new StringBuffer();
        if (args.length < 1) {
            log.error("use program name -ptype pvalue to run");
            System.exit(-1);
        } else {
            for (String arg : args) {
                paras.append(arg);
                paras.append(' ');
            }
        }

        Map<String, String> runPara = null;
        Configure conf = new Configure();
        try {
            setLogProperty(conf);
            runPara = ParserUtil.parser(null, paras.toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        final MainLaunch ml = new MainLaunch(runPara, conf);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (ml != null) {
                    try {
                        ml.start();
                    } catch (Exception e) {
                        log.error("start client error", e);
                        System.exit(-1);
                    }
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (ml != null) {
                    try {
                        ml.stop();
                    } catch (Exception e) {
                        log.error("start client error", e);
                        System.exit(-1);
                    }
                }
            }
        });
    }

    private static void setLogProperty(Configure conf) {
        if (conf == null) {
            throw new NullPointerException("Configure should not be null in setLogProperty");
        }
        String logPath = conf.getString(V.LOG_PATH);
        PropertyConfigurator.configure(logPath);
    }
}

