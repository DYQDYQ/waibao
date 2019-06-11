package com.cinsea.parsempp.ext;   

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource; 
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.mpp.MPPReader;


import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.io.*;
  
public class ParseProjectMpp   
{
    public static void main(String[] args) {

        List<TaskInfo> list = ParseProjectMpp.refreshTaskInfo(ParseProjectMpp.parseProjectMpp());
        for(TaskInfo taskinfo : list) {
            System.out.println(taskinfo.parent_id);
        }
        
       
    }
    
    public static class TaskInfo {

        private int project_id;                         // 所属项目ID
        private int task_id;                            // 任务ID
        private int task_unique_id;                     // 任务唯一ID
        private int parent_id;                          // 父任务ID
        private int task_outline_level;                 // 任务级别
        private String task_name;                       // 任务名称
        private double task_duration;                   // 任务工期
        private Date task_start_date;                   // 任务开始时间
        private Date task_finish_date;                  // 任务结束时间
        private String task_predecessors;               // 项目管理开始或者结束
        private String task_resource;                   //任务负责人
        
        public int getProject_id() {
            return project_id;
        }
        public void setProject_id(int project_id) {
            this.project_id = project_id;
        }
        public int getTask_id() {
            return task_id;
        }
        public void setTask_id(int task_id) {
            this.task_id = task_id;
        }
        public int getTask_unique_id() {
            return task_unique_id;
        }
        public void setTask_unique_id(int task_unique_id) {
            this.task_unique_id = task_unique_id;
        }
        public int getParent_id() {
            return parent_id;
        }
        public void setParent_id(int parent_id) {
            this.parent_id = parent_id;
        }
        public int getTask_outline_level() {
            return task_outline_level;
        }
        public void setTask_outline_level(int task_outline_level) {
            this.task_outline_level = task_outline_level;
        }
        public double getTask_duration() {
            return task_duration;
        }
        public void setTask_duration(double task_duration) {
            this.task_duration = task_duration;
        }
        public Date getTask_start_date() {
            return task_start_date;
        }
        public void setTask_start_date(Date task_start_date) {
            this.task_start_date = task_start_date;
        }
        public Date getTask_finish_date() {
            return task_finish_date;
        }
        public void setTask_finish_date(Date task_finish_date) {
            this.task_finish_date = task_finish_date;
        }
        public String getTask_predecessors() {
            return task_predecessors;
        }
        public void setTask_predecessors(String task_predecessors) {
            this.task_predecessors = task_predecessors;
        }
        public String getTask_resource() {
            return task_resource;
        }
        public void setTask_resource(String task_resource) {
            this.task_resource = task_resource;
        }
        public String getTask_name() {
            return task_name;
        }
        public void setTask_name(String task_name) {
            this.task_name = task_name;
        }
    }

    public static List<TaskInfo> parseProjectMpp() {
        
        List<TaskInfo> taskList = new ArrayList<TaskInfo>();
        
        String mppFilePath = "E:\\mpptest\\rrr.mpp";
        File file = new File(mppFilePath);
        MPPReader mppRead = new MPPReader();
        ProjectFile pf = null;
        try {
            pf = mppRead.read(file);
        } catch(MPXJException e) {
            e.printStackTrace();
        }
        List<Task> tasks = pf.getAllTasks();
        for(int i = 0; i < tasks.size();i++) {
            Task task = tasks.get(i);
            
            Integer task_id = task.getID();//项目id
            Integer task_unique_id = task.getUniqueID();
            Integer task_outline_level = task.getOutlineLevel();
            double task_duration = task.getDuration().getDuration();
            Date task_start_date = task.getStart();
            Date task_finish_date = task.getFinish();
            String task_name = task.getName();
            List<Relation> task_predecessors = task.getPredecessors();
            Date sqlStartDate = task_start_date; 
            Date sqlFinishDate = task_finish_date; 
            
            
            StringBuffer buf = new StringBuffer();
            StringBuffer PredecessorsStr = new StringBuffer();
            
            List<ResourceAssignment> assignments =  task.getResourceAssignments();          
            if(task_predecessors ==null){  

            }else{    
                if(task_predecessors != null){  
                    if(task_predecessors.size() > 0){  
                        for(Relation relation : task_predecessors){  
                            Integer targetTaskId = relation.getTargetTask().getID();  
                            if(PredecessorsStr.length() == 0){  
                                PredecessorsStr.append(targetTaskId);  
                            }else{  
                                PredecessorsStr.append(","+targetTaskId);  
                            }  
                        }  
                    }  
                }  
            }
            String Task_predecessors = PredecessorsStr.toString();
            
            for(ResourceAssignment assignment : assignments) {
                Resource resource = assignment.getResource();
                if (resource != null) {
                    buf.append(resource.getName()).append(",");
                }
            }
            String task_resource = buf.toString();
            
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setTask_id(task_id);
            taskInfo.setTask_unique_id(task_unique_id);
            taskInfo.setTask_outline_level(task_outline_level);
            taskInfo.setTask_duration(task_duration);
            taskInfo.setTask_start_date(task_start_date);
            taskInfo.setTask_finish_date(task_finish_date);
            taskInfo.setTask_predecessors(Task_predecessors);
            taskInfo.setTask_name(task_name);
            taskInfo.setTask_resource(task_resource);
            taskList.add(taskInfo);
            
//          System.out.println("MPXJUtils.method [readFile] taskInfo:" +"||"+"|||"+ task_id + "|" + task_unique_id + "|" + task_outline_level + "|" + task_duration + "|" + task_start_date + "|" + task_finish_date + "|" +"hh"+ task_resourse+"|"+task_name);

        }
        return taskList;
        
    }

    public static List<TaskInfo> refreshTaskInfo(List<TaskInfo> taskList){

        List<Map<String,Integer>> tempTaskOutLine = new ArrayList<Map<String,Integer>>();
        for(TaskInfo taskInfo : taskList){

            int taskId = taskInfo.getTask_id();
            int taskOutLineLevel = taskInfo.getTask_outline_level();            
            int listSize = tempTaskOutLine.size();
//            System.out.println("MPXJUtils.method [refreshTaskInfo1]: taskId-" + taskId + ",taskOutLineLevel-" + taskOutLineLevel + ",listSize-" + listSize);

            // 初始化taskOutLineLevel
            if(listSize > 2){               
                if(taskOutLineLevel == 1){                  
                    for(int i=listSize;i>2;i--){
                        tempTaskOutLine.remove(i-1);
                    }
                    listSize = 2;   
//                    System.out.println("MPXJUtils.method [refreshTaskInfo2]: taskId-" + taskId + ",taskOutLineLevel-" + taskOutLineLevel + ",listSize-" + listSize);
                }               
            }


            Map<String,Integer> map = new HashMap<String,Integer>();
            map.put("taskId", taskId);
            map.put("taskOutLineLevel", taskOutLineLevel);

            if(listSize == 0){

                if(taskOutLineLevel == 0){
                    tempTaskOutLine.add(map);
                }else{
                    return null;
                }

            }else{

                Map<String,Integer> lastMap = tempTaskOutLine.get(listSize-1);
                int lastTaskId = lastMap.get("taskId");
                int lastTaskOutLineLevel = lastMap.get("taskOutLineLevel");

                if(taskOutLineLevel > lastTaskOutLineLevel){

                    tempTaskOutLine.add(map);
                    taskInfo.setParent_id(lastTaskId);
                }else if(taskOutLineLevel == lastTaskOutLineLevel){ 

                    tempTaskOutLine.set(taskOutLineLevel, map);

                    Map<String,Integer> lastMap1 = tempTaskOutLine.get(taskOutLineLevel-1);
                    int lastTaskId1 = lastMap1.get("taskId");
                    taskInfo.setParent_id(lastTaskId1);
                }else if(taskOutLineLevel < lastTaskOutLineLevel){                  

                    tempTaskOutLine.set(taskOutLineLevel, map);

                    Map<String,Integer> lastMap2 = tempTaskOutLine.get(taskOutLineLevel-1);
                    int lastTaskId2 = lastMap2.get("taskId");
                    taskInfo.setParent_id(lastTaskId2);
                }
            }                       
        }

        return taskList;
    }
    
}