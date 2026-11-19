package kz.edu.fim.notification;
import kz.edu.fim.model.FileEvent;
public class AlertService { public void sendAlert(FileEvent e){System.out.println(EventLogger.format(e));} }
