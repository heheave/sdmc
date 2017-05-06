package runner;

import enums.ActorState;
import org.apache.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xiaoke on 17-5-6.
 */
public class Runner {

    private static final Logger log = Logger.getLogger(Runner.class);

    public static final int DEFAULT_MAILBOX_COMPACITY = 100;

    private final BlockingQueue<Message> mesMailBox;

    private final int mailBoxCompacity;

    private final ExecutorService executor;

    private final AtomicInteger state;

    private final boolean stopIfError;

    private volatile boolean alive;

    private volatile IActorRun run;

    public Runner(int mailBoxCompacity, boolean stopIfError, IActorRun run) {
        if (mailBoxCompacity <= 0) {
            mailBoxCompacity = DEFAULT_MAILBOX_COMPACITY;
        }
        this.stopIfError = stopIfError;
        this.mailBoxCompacity = mailBoxCompacity;
        this.alive = false;
        this.state = new AtomicInteger(0);
        this.run = run;
        this.mesMailBox = new ArrayBlockingQueue<Message>(mailBoxCompacity);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public Runner(int mailBoxCompacity, boolean stopIfError) {
        this(mailBoxCompacity, stopIfError, null);
    }

    public Runner() {
        this(Integer.MAX_VALUE, false, null);
    }

    public int getMailBoxCompacity() {
        return mailBoxCompacity;
    }

    public int getMailBoxSize() {
        if (this.state().equals(ActorState.RUNNING)) {
            return mesMailBox.size();
        } else {
            return 0;
        }

    }

    public void mailIn(Message mes) {
        mailIn(mes, true);
    }

    public boolean mailIn(Message mes, boolean isWaitIfFull) {
        if (!this.alive || this.state().equals(ActorState.STOPPED)) {
            throw new RuntimeException("Runner has been shutdown");
        }

        if (isWaitIfFull) {
            try {
                this.mesMailBox.put(mes);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return mesMailBox.offer(mes);
        }
    }

    public void start() {
        if (this.run == null) {
            throw new IllegalArgumentException("Runner run hasn't been set, cannot start it");
        }
        start0();
    }

    public void start(IActorRun irun) {
        if (irun != null && this.run == null) {
            this.run = irun;
        } else {
            throw new IllegalArgumentException("Runner run has been set, cannot be set again");
        }
        start0();
    }

    private void start0() {
        if(this.state().equals(ActorState.INITED)) {
            this.alive = true;
            this.state.incrementAndGet();
            executor.submit(new Runnable() {
                public void run() {
                    while(alive) {
                        try {
                            Message mes = mesMailBox.take();
                            if (mes != null && run != null) {
                                try {
                                    run.run(mes);
                                } catch (Exception e) {
                                    log.error(e);
                                    if (stopIfError) {
                                        stop();
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            log.error(e);
                        }
                    }
                }
            });
        } else {
            throw new IllegalThreadStateException("Runner has been stopped");
        }
    }

    public void stop() {
        this.alive = false;
        this.state.incrementAndGet();
        this.executor.shutdown();
    }

    public ActorState state() {
        int stateCode = this.state.get();
        return ActorState.values()[stateCode];
    }
}
