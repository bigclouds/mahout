package demo.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



















import ch.qos.logback.core.net.ObjectWriter;
import demo.PageRequestTools;
import demo.QueryModelMul;
import demo.pojo.Drycargo;
import demo.pojo.IndexOfGroup;
import demo.pojo.InfoOfMahout;
import demo.pojo.Fav;
import demo.pojo.MahoutOfCourse;
import demo.pojo.MahoutOfGroup;
import demo.pojo.MahoutOfType;
import demo.pojo.NewCourse;
import demo.pojo.NewGroupCourse;
import demo.pojo.Praise;
import demo.pojo.Topic;
import demo.pojo.User;
import demo.pojo.XueWenGroup;
import demo.pojo.redisuser;
import demo.service.GroupService;
import demo.service.InfoOfMahoutService;
import demo.service.DrycargoService;
import demo.service.FavService;
import demo.service.NewCourseService;
import demo.service.NewGroupCourseService;
import demo.service.PraiseService;
import demo.service.TopicService;
import demo.service.UserService;
import demo.service.mahoutOfCourseService;
import demo.service.mahoutOftypeService;

@RestController
@RequestMapping("/dry")
@Configuration
public class dryController {
	
	@Autowired
	public DrycargoService drycargoService;
	@Autowired
	InfoOfMahoutService infoOfMahoutService;
	@Autowired
	public UserService userService;
	@Autowired
	public FavService favService;
	@Autowired
	public mahoutOftypeService mahoutoftypeService;
	@Autowired
	public mahoutOfCourseService mahoutofCourseService;
	@Autowired
	public TopicService topicService;
	@Autowired
	public NewCourseService newCourseService;
	@Autowired
	public NewGroupCourseService newGroupCourse;
	@Autowired
	public PraiseService praiseService;
	@Autowired
	public GroupService groupService;
	 
	@Autowired
	RedisTemplate redisTemplate;
	
	 final static int NEIGHBORHOOD_NUM = 100;
	    final static int RECOMMENDER_NUM = 10;
	
	/**
	 * 搜索用户 条件：用户名，手机，邮箱
	 * 
	 * 第二部
	 * 根据数据库的收藏去解析推荐基础文档
	 * @param request
	 * @return
	 * @throws Exception 
	 */

	@RequestMapping("/findAllDry")
	public @ResponseBody Object searchbyinfo(HttpServletRequest request) throws Exception {
		File f = new File("/mahout/test2.csv");
		
		List<Object> lll=new ArrayList<Object>();
		lll.add("21");
		lll.add("01");
		lll.add("11");
		List<Fav> llll = favService.findBytypes(lll);
		
		List<Praise> lllll = praiseService.findBytypes(lll);
		
		 
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		int i = 0;
		for (Fav d : llll) {
			if (d.getUserId() != null) {

				User u = userService.findById(d.getUserId().toString());

				if (d.getSourceId() != null) {
					MahoutOfType mot = mahoutoftypeService.findone(d.getSourceId());

					if (mot != null) {
						
						
						try {
							writer.write(u.getUserNumber() + "," + mot.getIndex() + ",5" + "\r\n");
						} catch (Exception e) {
							// TODO: handle exception
							System.out.print(e);
						}	 
						
						
						
						
					}

				}

			}

		}
		
		
		for (Praise p : lllll) {
			if (p.getUserId() != null) {
				// writer.write(d.getAuthorId()+","+d.getId()+",5"+"\r\n");

				User u = userService.findById(p.getUserId().toString());

				if (p.getSourceId() != null) {
					MahoutOfType mot = mahoutoftypeService.findone(p.getSourceId());

					if (mot != null) {
					try {
						writer.write(u.getUserNumber() + "," + mot.getIndex() + ",5" + "\r\n");
					} catch (Exception e) {
						// TODO: handle exception
						System.out.print(e);
					}	 
							
						 
						
					}

				}

			}

		}

		
		

		writer.close();
		return true;
	}
	
	
	
	
	/**
	 * 群组
	 * 
	 * 第二部
	 * 根据数据库的收藏去解析推荐基础文档
	 * @param request
	 * @return
	 * @throws Exception 
	 */

	@RequestMapping("/findAllGroup")
	public @ResponseBody Object findAllGroup(HttpServletRequest request) throws Exception {
		File f = new File("/mahout/test.csv");
		
		List<XueWenGroup> l=groupService.findGroup();
		 
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		for (XueWenGroup d : l) {
			if (d.getMemberCount()!=0) {

				List<Object> menber=d.getMember();
					
				for(Object id:menber){
					User u = userService.findById(id.toString());
					if(u!=null){
						writer.write(u.getUserNumber() + "," + d.getGroupNumber() + ",5" + "\r\n");
					}
				}
			}

		}

		writer.close();
		return true;
	}
	
	
	
	
	/**
	 * 查询推荐基础文档，计算推荐,并存入数据库
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/findDry")
	public @ResponseBody Object findmostitems(HttpServletRequest request) throws Exception {
		 String file = "/mahout/test2.csv";
	        DataModel model = new FileDataModel(new File(file));
	        UserSimilarity user = new EuclideanDistanceSimilarity(model);
	        NearestNUserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, user, model);
	        Recommender r = new GenericUserBasedRecommender(model, neighbor, user);
	        LongPrimitiveIterator iter = model.getUserIDs();
	        
//	        redisTemplate.setKeySerializer(new StringRedisSerializer());  
//			redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());  
//			redisTemplate.afterPropertiesSet();  
//		  
//		    ValueOperations<String, Serializable> ops = redisTemplate.opsForValue(); 
	        
	        while (iter.hasNext()) {
	            long uid = iter.nextLong();
	            List<RecommendedItem> list =new ArrayList<RecommendedItem>();
	             list = r.recommend(uid, RECOMMENDER_NUM);
	            //System.out.printf("uid:%s", uid);
	            for (RecommendedItem ritem : list) {
	                //System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
	                long tmp=ritem.getItemID();
	                MahoutOfType m= mahoutoftypeService.findoneByindex(""+tmp);
	                
	                InfoOfMahout iom=new InfoOfMahout();
	                
	                iom.setSourceid(m.getSourceid());
	                iom.setType(m.getType());
	                iom.setUserid(""+uid);
	               
	                InfoOfMahout ii= infoOfMahoutService.save(iom);
//	                ops.set("mahout:collection:"+ii.getId()+":"+uid,ii);
	                //rediscontent.saveredis(iom);
	            }
	            //System.out.println();
	        }
		return true;
	}
	
	
	
	
	/**
	 * 群组
	 * 查询推荐基础文档，计算推荐,并存入数据库
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/findGroup")
	public @ResponseBody Object findGroup(HttpServletRequest request) throws Exception {
		 String file = "/mahout/test.csv";
	        DataModel model = new FileDataModel(new File(file));
	        UserSimilarity user = new EuclideanDistanceSimilarity(model);
	        NearestNUserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, user, model);
	        Recommender r = new GenericUserBasedRecommender(model, neighbor, user);
	        LongPrimitiveIterator iter = model.getUserIDs();
	        
//	        redisTemplate.setKeySerializer(new StringRedisSerializer());  
//			redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());  
//			redisTemplate.afterPropertiesSet();  
//		  
//		    ValueOperations<String, Serializable> ops = redisTemplate.opsForValue(); 
	        
	        
	        while (iter.hasNext()) {
	            long uid = iter.nextLong();
	            List<RecommendedItem> list =new ArrayList<RecommendedItem>();
	             list = r.recommend(uid, RECOMMENDER_NUM);
	            //System.out.printf("uid:%s", uid);
	            for (RecommendedItem ritem : list) {
	              //  System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
	               User u= userService.findBynumber(uid+"");
	              // groupService.findByMahoutGroup(usernumber)
	               if(u!=null){
	            	   MahoutOfGroup m=new MahoutOfGroup();
		                m.setGroupnumber(ritem.getItemID()+"");
		                m.setUsernumber(uid+"");
		                m.setUserid(u.getId());
		                MahoutOfGroup mhog= groupService.save(m);
//		                ops.set("mahout:collection:"+mhog.getId()+":uid",mhog);
	               }
	                
	            }
	        }
		return true;
	}
	
	
	
	/**
	 * 根据用户的number查询推荐的信息
	 * @param request
	 * @param unumber
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/findByuseid")
	public @ResponseBody Object findByuseid(HttpServletRequest request,String unumber,QueryModelMul dm) throws Exception {
		Pageable pageable = PageRequestTools.pageRequesMake(dm);
		Page<InfoOfMahout> m = infoOfMahoutService.findByusernumber(unumber,pageable);
		Drycargo d=null;
		Topic t=null;
		NewCourse n=null;
		NewGroupCourse ng=null;
		long l=mahoutoftypeService.count();
		long cl=mahoutofCourseService.count();
		Random random = new Random();
		List  ll=new ArrayList();
		int iii=m.getContent().size();
		if(m.getContent().size()==0||m.getContent().size()<10){
			Map m2=new HashMap();
			User u=userService.findBynumber(unumber);
			 
		for(int i=0;i<10;i++){
			Map m3=new HashMap();
			int num=0;
			
			if(i<5){
				num=random.nextInt(Integer.parseInt(l/2+""))+1;
			}
			else {
				num=random.nextInt(Integer.parseInt(l/2+""))+(int)(l/2-1);
			}
			
			
			//ll.add(num);
			MahoutOfType mm=mahoutoftypeService.findoneByindex(num+"");
			
			if("dry".equals(mm.getType())){
				d=drycargoService.findByid(mm.getSourceid());
				if(u==null){
					d.setFeelgood("0");
				}
				else {
					if(d!=null){
						boolean b=praiseService.findByuseridandsourceid(u.getId(),d.getId());
						if(b){
							d.setFeelgood("1");
						}
						else {
							d.setFeelgood("0");
						}
					}
					else {
						continue;
					}
					
				}
				d.setType("dry");
				ll.add(d);
			}
			if("topic".equals(mm.getType())){
				t=topicService.findByid(mm.getSourceid());	
				if(u==null){
					t.setFeelgood("0");
				}
				else {
					if(t!=null){
						boolean b=praiseService.findByuseridandsourceid(u.getId(),t.getTopicId());
						if(b){
							t.setFeelgood("1");
						}
						else {
							t.setFeelgood("0");
						}
					}
					else {
						continue;
					}
					
				}
				t.setType("topic");
				ll.add(t);
			}
			
		}
		
		
		int coursenum=0;
		coursenum=random.nextInt(Integer.parseInt(cl+""))+1;
		MahoutOfCourse cm=mahoutofCourseService.findoneByindex(coursenum+"");
		
		if("course".equals(cm.getType())){
			ng=newGroupCourse.findGroupCourse(cm.getSourceid());
			NewCourse nn=newCourseService.findByid(ng.getCourse().toString());
			ng.setNewCourse(nn);
			if(u==null){
				ng.setFeelgood("0");
			}
			else {
				if(ng!=null&&nn!=null){
					boolean b=praiseService.findByuseridandsourceid(u.getId(),ng.getId());
					if(b){
						ng.setFeelgood("1");
					}
					else {
						ng.setFeelgood("0");
					}
				}
				
			}
			ng.setType("course");
			ll.add(ng);
		}
		
		
		
		m2.put("user", u);
		m2.put("result", ll);
		//System.out.println(ll);
		return m2;
		}
		
		else {
			List l1=new ArrayList();
			Map m1=new HashMap();
			List<InfoOfMahout> lnfo=m.getContent();
			User u=userService.findBynumber(unumber);
			for(InfoOfMahout i:lnfo){
				Map m4=new HashMap();
				if("dry".equals(i.getType())){
					d=drycargoService.findByid(i.getSourceid());
					if(u==null){
						d.setFeelgood("0");
					}
					else {
						if(d!=null){
							boolean b=praiseService.findByuseridandsourceid(u.getId(),d.getId());
							if(b){
								d.setFeelgood("1");
							}
							else {
								d.setFeelgood("0");
							}
						}
						else {
							continue;
						}
						
					}
					d.setType("dry");
					l1.add(d);
				}
				if("topic".equals(i.getType())){
					t=topicService.findByid(i.getSourceid());	
					if(u==null){
						t.setFeelgood("0");
					}
					else {
						if(t!=null){
							boolean b=praiseService.findByuseridandsourceid(u.getId(),t.getTopicId());
							if(b){
								t.setFeelgood("1");
							}
							else {
								t.setFeelgood("0");
							}
						}
						else {
							continue;
						}
						
					}
					t.setType("topic");
					l1.add(t);
				}
				if("course".equals(i.getType())){
					ng=newGroupCourse.findGroupCourse(i.getSourceid());
					NewCourse nn=newCourseService.findByid(ng.getCourse().toString());
					
					if(u==null){
						ng.setFeelgood("0");
					}
					else {
						if(ng!=null&&nn!=null){
							boolean b=praiseService.findByuseridandsourceid(u.getId(),ng.getId());
							if(b){
								ng.setFeelgood("1");
							}
							else {
								ng.setFeelgood("0");
							}
						}
						else {
							continue;
						}
						
					}
					ng.setType("course");
					ng.setNewCourse(nn);
					l1.add(ng);
				}
			}
			m1.put("user", u);
			m1.put("result", l1);
		 
			return m1;
		}
		
		
	}
	
	
	
	
	
	
	/**
	 * 群组
	 * 根据用户的number查询推荐的信息
	 * @param request
	 * @param unumber
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/findGroupByuseid")
	public @ResponseBody Object findGroupByuseid(HttpServletRequest request,String unumber) throws Exception {
		
		List<MahoutOfGroup> m = groupService.findByMahoutGroup(unumber);
		
		//User u=userService.findBynumber(unumber);
		long l=groupService.count();
		Random random = new Random();
		List  ll=new ArrayList();
		Map m1=new HashMap();
		
		if(m.size()==0||m.size()<10){
		for(int i=0;i<15;i++){
			int num=0;
			num=random.nextInt(Integer.parseInt(l+""))+1;
			//ll.add(num);
			//MahoutOfType mm=ma.findoneByindex(num+"");
			IndexOfGroup mm =groupService.findbyindex(num+"");
			XueWenGroup x=groupService.findbyid(mm.getGroupid());
			mm.setGroupid(x.getId());
			mm.setGroupNumber(x.getGroupNumber()+"");
			mm.setUsernumber(unumber);
			mm.setCtime(x.getCtime()+"");
			mm.setLogoUrl(x.getLogoUrl());
			mm.setIntro(x.getIntro());
			mm.setGroupName(x.getGroupName());
			ll.add(mm);
			
		}
		//System.out.println(ll);
		m1.put("result", ll);
		//m1.put("user", u);
		return m1;
		}
		else {
			
			for(MahoutOfGroup m2:m){
				XueWenGroup x=groupService.findbygroupnumber(m2.getGroupnumber());
				m2.setGroupid(x.getId());
				m2.setGroupNumber(x.getGroupNumber()+"");
				m2.setUsernumber(unumber);
				m2.setCtime(x.getCtime()+"");
				m2.setLogoUrl(x.getLogoUrl());
				m2.setGroupName(x.getGroupName());
				m2.setIntro(x.getIntro());
				ll.add(m2);
			}
			
			m1.put("result", ll);
			//m1.put("user", u);
			return m1;
		}
		
	}
	
	
	@RequestMapping("/testsave")
	private void test(HttpServletRequest request) throws Exception {
		 redisTemplate.setKeySerializer(new StringRedisSerializer());  
			redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());  
			redisTemplate.afterPropertiesSet();  
		  
		    ValueOperations<String, Serializable> ops = redisTemplate.opsForValue(); 
		// TODO Auto-generated method stub
		redisuser u=new redisuser();
		u.setId("111");
		u.setName("hehe");
		u.setPassword("222");
		    
		
		redisuser u2=new redisuser();
		u2.setId("222");
		u2.setName("xixi");
		u2.setPassword("333");
		ops.set("mahout:test:1", u);
		ops.set("mahout:test:2", u2);
	}
	
	
	@RequestMapping("/testget")
	private void testget(HttpServletRequest request) throws Exception {
		 redisTemplate.setKeySerializer(new StringRedisSerializer());  
			redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());  
			redisTemplate.afterPropertiesSet();  
		  
		    ValueOperations<String, Serializable> ops = redisTemplate.opsForValue(); 
		// TODO Auto-generated method stub
		    redisuser r=(redisuser) ops.get("mahout:test:1");
		    List ll=new ArrayList();
		    ll.add("mahout:test:1");
		    ll.add("mahout:test:2");
		  List<Serializable> l= ops.multiGet(ll);
		   
		  
		  for(Object j:l){
			  redisuser rr=(redisuser)j;
			  System.out.println(rr.getName()+rr.getPassword());
		  }
		    
//		 if(r!=null){
			 System.out.println(ops.size("mahout:test:1"));
//		 }
		
	}
	
	
	@RequestMapping("/testdel")
	private void testdel(HttpServletRequest request) throws Exception {
		redisTemplate.delete("mahout:test:1");
		   
		 
		    
	}
	
	
	
	
//	/**
//	 * 根据用户的number查询推荐的信息
//	 * @param <T>
//	 * @param request
//	 * @param unumber
//	 * @return 
//	 * @return
//	 * @throws Exception
//	 */
//	 
//	public  void saveredis(Object o) throws Exception {
//		redisTemplate.setKeySerializer(new StringRedisSerializer());  
//		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());  
//		redisTemplate.afterPropertiesSet();  
//	  
//	    ValueOperations<String, Serializable> ops = redisTemplate.opsForValue();  
//	  
////		redisuser u=new redisuser();
////		u.setId("111");
////		u.setName("hehe");
////		u.setPassword("222");
//		 ops.set("mahout:collection",(Serializable) o);
//		 
//		
//	}
//	
//	
//	/**
//	 * 根据用户的number查询推荐的信息
//	 * @param request
//	 * @param unumber
//	 * @return
//	 * @throws Exception
//	 */
//	 
//	public  Object getByuseidonredis() throws Exception {
//		redisTemplate.setKeySerializer(new StringRedisSerializer());  
//		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());  
//		redisTemplate.afterPropertiesSet();  
//	  
//	    ValueOperations<String, Serializable> ops = redisTemplate.opsForValue();  
//	  
//		 
//		Object u= ops.get("mahout:collection");
//		return u;
//		
//	}
	
	/**
	 * 第一步
	 * 把库里所有干活，话题，课程全部查出来按照序列存入库里，这是第一步
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/init")
	public @ResponseBody Object init(HttpServletRequest request) throws Exception {
		 
		 List<Drycargo> drys = drycargoService.findByDryFlag();
		 List<Topic> topic = topicService.all();
		 List<NewGroupCourse> ng=newGroupCourse.findAllGroupCourse();
		 List<XueWenGroup> group=groupService.findGroup();
		 mahoutoftypeService.delete();
		 mahoutofCourseService.delete();
		 groupService.delete();
		 infoOfMahoutService.delete();
		 groupService.delete2();
		int i=1;
		for(Drycargo d:drys){
			MahoutOfType m=new MahoutOfType();
			m.setSourceid(d.getId());
			m.setIndex(""+i++);
			m.setType("dry");
			mahoutoftypeService.save(m); 
			}
		
		for(Topic t:topic){
			MahoutOfType m=new MahoutOfType();
			m.setSourceid(t.getTopicId());
			m.setIndex(""+i++);
			m.setType("topic");
			mahoutoftypeService.save(m); 
			}
		
		int kk=1;
		for(NewGroupCourse c:ng){
		
			MahoutOfCourse m=new MahoutOfCourse();
			m.setSourceid(c.getId());
			m.setIndex(""+kk++);
			m.setType("course");
			mahoutofCourseService.save(m); 
			}
		int j=1;
		for(XueWenGroup g:group){
			
			IndexOfGroup k=new IndexOfGroup();
			k.setIndex(""+j++);
			k.setGroupid(g.getId());
			groupService.saveindexofgroup(k); 
			}
			
		return true;
	}
	
	
}
