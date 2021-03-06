/**
 * Copyright &copy; 2012-2014 <a href="https://github.com.bra.>JeeSite</a> All rights reserved.
 */
package com.bra.modules.cms.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bra.common.mapper.JsonMapper;
import com.bra.common.persistence.Page;
import com.bra.common.utils.StringUtils;
import com.bra.common.web.BaseController;
import com.bra.modules.cms.entity.Article;
import com.bra.modules.cms.entity.Category;
import com.bra.modules.cms.entity.Site;
import com.bra.modules.cms.service.*;
import com.bra.modules.cms.utils.CmsUtils;
import com.bra.modules.cms.utils.TplUtils;
import com.bra.modules.mechanism.web.bean.AttMainForm;
import com.bra.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 文章Controller
 *
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/article")
public class ArticleController extends BaseController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleDataService articleDataService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private FileTplService fileTplService;
    @Autowired
    private SiteService siteService;

    @ModelAttribute
    public Article get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return articleService.get(id);
        } else {
            return new Article();
        }
    }

    @RequiresPermissions("cms:article:view")
    @RequestMapping(value = {"list", ""})
    public String list(Article article, HttpServletRequest request, HttpServletResponse response, Model model) {
//		for (int i=0; i<10000000; i++){
//			Article a = new Article();
//			a.setCategory(new Category(article.getCategory().getId()));
//			a.setTitle("测试测试测试测试测试测试测试测试"+a.getCategory().getId());
//			a.setArticleData(new ArticleData());
//			a.getArticleData().setContent(a.getTitle());
//			articleService.save(a);
//		}
        Page<Article> page = articleService.findPage(new Page<Article>(request, response), article, true);
        model.addAttribute("page", page);
        return "modules/cms/articleList";
    }

    @RequiresPermissions("cms:article:view")
    @RequestMapping(value = "form")
    public String form(Article article, Model model) {
        // 如果当前传参有子节点，则选择取消传参选择
        if (article.getCategory() != null && StringUtils.isNotBlank(article.getCategory().getId())) {
            List<Category> list = categoryService.findByParentId(article.getCategory().getId(), Site.getCurrentSiteId());
            if (list.size() > 0) {
                article.setCategory(null);
            } else {
                article.setCategory(categoryService.get(article.getCategory().getId()));
            }
        }
        article.setArticleData(articleDataService.get(article.getId()));
//		if (article.getCategory()=null && StringUtils.isNotBlank(article.getCategory().getId())){
//			Category category = categoryService.get(article.getCategory().getId());
//		}
        model.addAttribute("contentViewList", getTplContent());
        model.addAttribute("article_DEFAULT_TEMPLATE", Article.DEFAULT_TEMPLATE);
        model.addAttribute("article", article);
        CmsUtils.addViewConfigAttribute(model, article.getCategory());
        return "modules/cms/articleForm";
    }

    @RequiresPermissions("cms:article:edit")
    @RequestMapping(value = "save")
    public String save(Article article, AttMainForm attMainForm, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, article)) {
            return form(article, model);
        }
        try {
            articleService.save(article, attMainForm);
            addMessage(redirectAttributes, "保存文章'" + StringUtils.abbr(article.getTitle(), 50) + "'成功");
        }catch (Exception e){
            addMessage(redirectAttributes, e.getMessage()+"保存文章'" + StringUtils.abbr(article.getTitle(), 50) + "'失败");
        }
        String categoryId = article.getCategory() != null ? article.getCategory().getId() : null;
        return "redirect:" + adminPath + "/cms/article/?repage&category.id=" + (categoryId != null ? categoryId : "");
    }

    @RequiresPermissions("cms:article:edit")
    @RequestMapping(value = "delete")
    public String delete(Article article, String categoryId, @RequestParam(required = false) Boolean isRe, RedirectAttributes redirectAttributes) {
        // 如果没有审核权限，则不允许删除或发布。
        if (!UserUtils.getSubject().isPermitted("cms:article:audit")) {
            addMessage(redirectAttributes, "你没有删除或发布权限");
        }
        articleService.delete(article, isRe);
        addMessage(redirectAttributes, (isRe != null && isRe ? "发布" : "删除") + "文章成功");
        return "redirect:" + adminPath + "/cms/article/?repage&category.id=" + (categoryId != null ? categoryId : "");
    }

    /**
     * 文章选择列表
     */
    @RequiresPermissions("cms:article:view")
    @RequestMapping(value = "selectList")
    public String selectList(Article article, HttpServletRequest request, HttpServletResponse response, Model model) {
        list(article, request, response, model);
        return "modules/cms/articleSelectList";
    }

    /**
     * 通过编号获取文章标题
     */
    @RequiresPermissions("cms:article:view")
    @ResponseBody
    @RequestMapping(value = "findByIds")
    public String findByIds(String ids) {
        List<Object[]> list = articleService.findByIds(ids);
        return JsonMapper.nonDefaultMapper().toJson(list);
    }

    private List<String> getTplContent() {
        List<String> tplList = fileTplService.getNameListByPrefix(siteService.get(Site.getCurrentSiteId()).getSolutionPath());
        tplList = TplUtils.tplTrim(tplList, Article.DEFAULT_TEMPLATE, "");
        return tplList;
    }
    @RequestMapping(value = "app/getList")
    public void getArticleList(Article article,HttpServletRequest request,HttpServletResponse response){
        Page<Article> page = new Page<Article>(request, response);
        List<Map<String,Object>> rtn = articleService.findListMap(page, article);
        try {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(JSONArray.toJSONString(rtn, SerializerFeature.WriteMapNullValue));
        } catch (IOException e) {
        }
    }

    @RequiresPermissions("cms:article:edit")
    @RequestMapping(value = "deleteAll")
    @ResponseBody
    public String deleteAll(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String[] ida = request.getParameterValues("ids[]");
        String del = " id in (";
        if(ida!=null&&ida.length>0){
            for(int i =0;i<ida.length;i++){
                if(i<ida.length-1)
                    del+="'"+ida[i]+"',";
                else
                    del+="'"+ida[i]+"')";
            }
        }
        Article a = new Article();
        a.getSqlMap().put("del",del);
        articleService.deleteAll(a);
        return "删除成功";
    }
}
