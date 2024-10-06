package dev.coms4156.project.individualproject.service;

import dev.coms4156.project.individualproject.entity.Course;
import dev.coms4156.project.individualproject.entity.Department;
import java.util.HashMap;
import java.util.Map;

/**
 * Class contains logic to populate database with starter data.
 */
public final class DatabaseService {

  private DatabaseService() {
    // no-op
  }

  /**
   * Creates a new MyFileDatabase that will be saved
   * when Java Spring shuts down.
   *
   * @return MyFileDatabase
   */
  public static MyFileDatabase createMyFileDatabaseFromDataFile() {
    return new MyFileDatabase(0, "/data.txt");
  }

  /**
   * Creates a new MyFileDatabase that will not be saved
   * when Java Spring shuts down.
   *
   * @return MyFileDatabase
   */
  public static MyFileDatabase resetDataFile() {
    MyFileDatabase myFileDatabase = new MyFileDatabase(1, "./data.txt");

    Map<String, Department> mapping = createDatabaseMapping();
    myFileDatabase.setMapping(mapping);

    return myFileDatabase;
  }

  /** Allows for data to be reset in event of errors. */
  public static Map<String, Department> createDatabaseMapping() {
    String[] times = {"11:40-12:55", "4:10-5:25", "10:10-11:25", "2:40-3:55"};
    String[] locations = {"417 IAB", "309 HAV", "301 URIS"};

    // data for coms dept
    Course coms1004 = new Course("Adam Cannon", locations[0], times[0], 400);
    coms1004.setEnrolledStudentCount(249);
    Course coms3134 = new Course("Brian Borowski", locations[2], times[1], 250);
    coms3134.setEnrolledStudentCount(242);
    Course coms3157 = new Course("Jae Lee", locations[0], times[1], 400);
    coms3157.setEnrolledStudentCount(311);
    Course coms3203 = new Course("Ansaf Salleb-Aouissi", locations[2], times[2], 250);
    coms3203.setEnrolledStudentCount(215);
    Course coms3261 = new Course("Josh Alman", locations[0], times[3], 150);
    coms3261.setEnrolledStudentCount(140);
    Course coms3251 = new Course("Tony Dear", "402 CHANDLER", "1:10-3:40", 125);
    coms3251.setEnrolledStudentCount(99);
    Course coms3827 = new Course("Daniel Rubenstein", "207 Math", times[2], 300);
    coms3827.setEnrolledStudentCount(283);
    Course coms4156 = new Course("Gail Kaiser", "501 NWC", times[2], 120);
    coms4156.setEnrolledStudentCount(109);
    Map<String, Course> courses = new HashMap<>();
    courses.put("1004", coms1004);
    courses.put("3134", coms3134);
    courses.put("3157", coms3157);
    courses.put("3203", coms3203);
    courses.put("3261", coms3261);
    courses.put("3251", coms3251);
    courses.put("3827", coms3827);
    courses.put("4156", coms4156);
    Department compSci = new Department("COMS", courses, "Luca Carloni", 2700);
    Map<String, Department> mapping = new HashMap<>();
    mapping.put("COMS", compSci);

    // data for econ dept
    Course econ1105 = new Course("Waseem Noor", locations[1], times[3], 210);
    econ1105.setEnrolledStudentCount(187);
    Course econ2257 = new Course("Tamrat Gashaw", "428 PUP", times[2], 125);
    econ2257.setEnrolledStudentCount(63);
    Course econ3211 = new Course("Murat Yilmaz", "310 FAY", times[1], 96);
    econ3211.setEnrolledStudentCount(81);
    Course econ3213 = new Course("Miles Leahey", "702 HAM", times[1], 86);
    econ3213.setEnrolledStudentCount(77);
    Course econ3412 = new Course("Thomas Piskula", "702 HAM", times[0], 86);
    econ3412.setEnrolledStudentCount(81);
    Course econ4415 = new Course("Evan D Sadler", locations[1], times[2], 110);
    econ4415.setEnrolledStudentCount(63);
    Course econ4710 = new Course("Matthieu Gomez", "517 HAM", "8:40-9:55", 86);
    econ4710.setEnrolledStudentCount(37);
    Course econ4840 = new Course("Mark Dean", "142 URIS", times[3], 108);
    econ4840.setEnrolledStudentCount(67);

    courses = new HashMap<>();
    courses.put("1105", econ1105);
    courses.put("2257", econ2257);
    courses.put("3211", econ3211);
    courses.put("3213", econ3213);
    courses.put("3412", econ3412);
    courses.put("4415", econ4415);
    courses.put("4710", econ4710);
    courses.put("4840", econ4840);

    Department econ = new Department("ECON", courses, "Michael Woodford", 2345);
    mapping.put("ECON", econ);

    // data for ieor dept
    Course ieor2500 = new Course("Uday Menon", "627 MUDD", times[0], 50);
    ieor2500.setEnrolledStudentCount(52);
    Course ieor3404 = new Course("Christopher J Dolan", "303 MUDD", times[2], 73);
    ieor3404.setEnrolledStudentCount(80);
    Course ieor3658 = new Course("Daniel Lacker", "310 FAY", times[2], 96);
    ieor3658.setEnrolledStudentCount(87);
    Course ieor4102 = new Course("Antonius B Dieker", "209 HAM", times[2], 110);
    ieor4102.setEnrolledStudentCount(92);
    Course ieor4106 = new Course("Kaizheng Wang", "501 NWC", times[2], 150);
    ieor4106.setEnrolledStudentCount(161);
    Course ieor4405 = new Course("Yuri Faenza", "517 HAV", times[0], 80);
    ieor4405.setEnrolledStudentCount(19);
    Course ieor4511 = new Course("Michael Robbins", "633 MUDD", "9:00-11:30", 150);
    ieor4511.setEnrolledStudentCount(50);
    Course ieor4540 = new Course("Krzysztof M Choromanski", "633 MUDD", "7:10-9:40", 60);
    ieor4540.setEnrolledStudentCount(33);

    courses = new HashMap<>();
    courses.put("2500", ieor2500);
    courses.put("3404", ieor3404);
    courses.put("3658", ieor3658);
    courses.put("4102", ieor4102);
    courses.put("4106", ieor4106);
    courses.put("4405", ieor4405);
    courses.put("4511", ieor4511);
    courses.put("4540", ieor4540);

    Department ieor = new Department("IEOR", courses, "Jay Sethuraman", 67);
    mapping.put("IEOR", ieor);

    // data for chem dept
    Course chem1403 = new Course("Ruben M Savizky", locations[1], "6:10-7:25", 120);
    chem1403.setEnrolledStudentCount(100);
    Course chem1500 = new Course("Joseph C Ulichny", "302 HAV", "6:10-9:50", 46);
    chem1500.setEnrolledStudentCount(50);
    Course chem2045 = new Course("Luis M Campos", "209 HAV", "1:10-2:25", 50);
    chem2045.setEnrolledStudentCount(29);
    Course chem2444 = new Course("Christopher Eckdahl", locations[1], times[0], 150);
    chem2444.setEnrolledStudentCount(150);
    Course chem2494 = new Course("Talha Siddiqui", "202 HAV", "1:10-5:00", 24);
    chem2494.setEnrolledStudentCount(18);
    Course chem3080 = new Course("Milan Delor", "209 HAV", times[2], 60);
    chem3080.setEnrolledStudentCount(18);
    Course chem4071 = new Course("Jonathan S Owen", "320 HAV", "8:40-9:55", 42);
    chem4071.setEnrolledStudentCount(29);
    Course chem4102 = new Course("Dalibor Sames", "320 HAV", times[2], 28);
    chem4102.setEnrolledStudentCount(27);

    courses = new HashMap<>();
    courses.put("1403", chem1403);
    courses.put("1500", chem1500);
    courses.put("2045", chem2045);
    courses.put("2444", chem2444);
    courses.put("2494", chem2494);
    courses.put("3080", chem3080);
    courses.put("4071", chem4071);
    courses.put("4102", chem4102);

    Department chem = new Department("CHEM", courses, "Laura J. Kaufman", 250);
    mapping.put("CHEM", chem);

    // data for phys dept
    Course phys1001 = new Course("Szabolcs Marka", "301 PUP", times[3], 150);
    phys1001.setEnrolledStudentCount(131);
    Course phys1201 = new Course("Eric Raymer", "428 PUP", times[3], 145);
    phys1201.setEnrolledStudentCount(130);
    Course phys1602 = new Course("Kerstin M Perez", "428 PUP", times[2], 140);
    phys1602.setEnrolledStudentCount(77);
    Course phys2802 = new Course("Yury Levin", "329 PUP", "10:10-12:00", 60);
    phys2802.setEnrolledStudentCount(23);
    Course phys3008 = new Course("William A Zajc", "329 PUP", times[2], 75);
    phys3008.setEnrolledStudentCount(60);
    Course phys4003 = new Course("Frederik Denef", "214 PUP", times[1], 50);
    phys4003.setEnrolledStudentCount(19);
    Course phys4018 = new Course("James W McIver", "307 PUP", times[3], 30);
    phys4018.setEnrolledStudentCount(18);
    Course phys4040 = new Course("James C Hill", "214 PUP", times[1], 50);
    phys4040.setEnrolledStudentCount(31);

    courses = new HashMap<>();
    courses.put("2802", phys2802);
    courses.put("3008", phys3008);
    courses.put("4003", phys4003);
    courses.put("4018", phys4018);
    courses.put("4040", phys4040);
    courses.put("1602", phys1602);
    courses.put("1001", phys1001);
    courses.put("1201", phys1201);

    Department phys = new Department("PHYS", courses, "Dmitri N. Basov", 43);
    mapping.put("PHYS", phys);

    // data for elen dept
    Course elen1201 = new Course("David G Vallancourt", "301 PUP", times[1], 120);
    elen1201.setEnrolledStudentCount(108);
    Course elen3082 = new Course("Kenneth Shepard", "1205 MUDD", "4:10-6:40", 32);
    elen3082.setEnrolledStudentCount(30);
    Course elen3331 = new Course("David G Vallancourt", "203 MATH", times[0], 80);
    elen3331.setEnrolledStudentCount(54);
    Course elen3401 = new Course("Keren Bergman", "829 MUDD", times[3], 40);
    elen3401.setEnrolledStudentCount(25);
    Course elen3701 = new Course("Irving Kalet", "333 URIS", times[3], 50);
    elen3701.setEnrolledStudentCount(24);
    Course elen4510 = new Course("Mohamed Kamaludeen", "903 SSW", "7:00-9:30", 30);
    elen4510.setEnrolledStudentCount(22);
    Course elen4702 = new Course("Alexei Ashikhmin", "332 URIS", "7:00-9:30", 50);
    elen4702.setEnrolledStudentCount(5);
    Course elen4830 = new Course("Christine P Hendon", "633 MUDD", "10:10-12:40", 60);
    elen4830.setEnrolledStudentCount(22);

    courses = new HashMap<>();
    courses.put("1201", elen1201);
    courses.put("3082", elen3082);
    courses.put("3331", elen3331);
    courses.put("3401", elen3401);
    courses.put("3701", elen3701);
    courses.put("4510", elen4510);
    courses.put("4702", elen4702);
    courses.put("4830", elen4830);

    Department elen = new Department("ELEN", courses, "Ioannis Kymissis", 250);
    mapping.put("ELEN", elen);

    // data for psyc dept
    Course psyc1001 = new Course("Patricia G Lindemann", "501 SCH", "1:10-2:25", 200);
    psyc1001.setEnrolledStudentCount(191);
    Course psyc1610 = new Course("Christopher Baldassano", "200 SCH", times[2], 45);
    psyc1610.setEnrolledStudentCount(42);
    Course psyc2235 = new Course("Katherine T Fox-Glassman", "501 SCH", times[0], 125);
    psyc2235.setEnrolledStudentCount(128);
    Course psyc2620 = new Course("Jeffrey M Cohen", "303 URIS", "1:10-3:40", 60);
    psyc2620.setEnrolledStudentCount(55);
    Course psyc3212 = new Course("Mayron Piccolo", "200 SCH", "2:10-4:00", 15);
    psyc3212.setEnrolledStudentCount(15);
    Course psyc3445 = new Course("Mariam Aly", "405 SCH", "2:10-4:00", 12);
    psyc3445.setEnrolledStudentCount(12);
    Course psyc4236 = new Course("Trenton Jerde", "405 SCH", "6:10-8:00", 18);
    psyc4236.setEnrolledStudentCount(17);
    Course psyc4493 = new Course("Jennifer Blaze", "200 SCH", "2:10-4:00", 15);
    psyc4493.setEnrolledStudentCount(9);

    courses = new HashMap<>();
    courses.put("1001", psyc1001);
    courses.put("1610", psyc1610);
    courses.put("2235", psyc2235);
    courses.put("2620", psyc2620);
    courses.put("3212", psyc3212);
    courses.put("3445", psyc3445);
    courses.put("4236", psyc4236);
    courses.put("4493", psyc4493);

    Department psyc = new Department("PSYC", courses, "Nim Tottenham", 437);
    mapping.put("PSYC", psyc);

    return mapping;
  }
}
