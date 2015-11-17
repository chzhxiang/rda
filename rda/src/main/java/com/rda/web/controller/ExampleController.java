/*******************************************************************************
 * rda
 ******************************************************************************/
package com.rda.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.rda.app.domain.model.User;
import com.rda.app.services.UserService;
import com.rda.web.base.controller.BaseController;
import com.rda.web.constants.Urls;


/**
 * <P>配置Request Url的多种方式</P>
 * @author lianrao
 */
@Controller
@RequiresAuthentication
public class ExampleController extends BaseController {

	private AtomicInteger id = new AtomicInteger(1000);
	
	@Autowired
	@Qualifier("myUserService")
	private UserService service;

	/**
	 * 
	 * <p>http get for /user invokes this method</p>
	 * @param model
	 * @return
	 * @author lianrao
	 */
@RequestMapping(value = Urls.USER_GET_URL, method = RequestMethod.GET)
	public String getMethod(ModelMap model) {
		log.debug("getUser invoked!");
		model.addAttribute("userName", service.getUser(1));
		return "user";
	}
	
	@RequestMapping(value = "/user/add", method = RequestMethod.GET)
	public String addUser(Model model){
		log.debug("add user invoked!");
		User user = new User();
		user.setId(id.intValue());
		user.setName("adduser"+id.intValue());
		service.addUser(id.intValue(),"addUser" + id.intValue());
		id.incrementAndGet();
		model.addAttribute("userName",user); 
		return "user";
	}

	/**
	 * 
	 * <p>user uri template</p>
	 * @param id
	 * @param model
	 * @return
	 * @author lianrao
	 */
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public String getMethodWithUriTemlate(@PathVariable("id") int id, Model model) {
		model.addAttribute("userName", service.getUser(id));
		return "user";
	}

	/**
	 * 
	 * <p>url可以从properties文件中取</p>
	 * @return
	 * @author lianrao
	 */
	@RequestMapping(value = "${urls.user}", method = RequestMethod.GET)
	public String getMethodWithUrlFromProperties() {
		//		model.addAttribute("userName", service.getUser(id));
		return "user";
	}

	/**
	 * 
	 * <p>spring mvc3.2 支持异步request处理;</p>
	 * @param file
	 * @return
	 * @author lianrao
	 */
	@RequestMapping(value = "/asyc/fileUpload", method = RequestMethod.POST)
	public Callable<String> asyncPostMethod(final MultipartFile file) {
		return new Callable<String>() {
			public String call() throws Exception {
				// ...
				return "someView";
			}
		};
	}

	/**
	 * 
	 * <p>使用flashAttributes in post/redirect/get 操作</p>
	 * @return
	 * @author lianrao
	 */
	@RequestMapping(value = "/flash/post", method = RequestMethod.POST)
	public String postMeothdWithFlash(Model model, Map<String, String> map) {
		return "redirect:/other";
	}

	/**
	 * 
	 * <p>和flashAttrUse配合使用</p>
	 * @return
	 * @author lianrao
	 */
	@RequestMapping(value = "/other", method = RequestMethod.GET)
	public String getMethodWithFlash() {
		return "otherInfo";
	}

	/**
	 * 
	 * <p>文件上传</p>
	 * @param file
	 * @return
	 * @author lianrao
	 * @throws IOException 
	 */
	@RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
	public String fileUpload(@RequestParam("fieldNameHere") MultipartFile file) throws IOException {
		
		InputStream inputStream = file.getInputStream();
		return "redirect:/html/success.html";
	}
	
	
	@RequestMapping(value="/ajax/user.json",method=RequestMethod.POST)
	public User ajaxMethod(@RequestBody @Valid User user){
		User user1 = service.getUser(user.getId());
		return user1;
	}
	
	
	@RequestMapping(value="/responseBody",method=RequestMethod.GET)
	@ResponseBody
	public String responseBody(){
		return "hello world";
	}
	
	@RequestMapping(value="/jxls" ,method=RequestMethod.GET)
	public String jxlsView(ModelMap map){
		User user = service.getUser(1);
		map.put("name", user.getName());
		return "excel.myExcel.view";
	}
	
}
