/*     */ package com.rda.web.base.wrapper;
/*     */ 
/*     */ import java.util.Random;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpServletResponseWrapper;
/*     */ 
/*     */ class CsrfResponseWrapper extends HttpServletResponseWrapper
/*     */ {
/*  12 */   private static String randomClass = "java.security.SecureRandom";
/*     */   private static Random randomSource;
/*     */ 
/*     */   public CsrfResponseWrapper(HttpServletResponse response)
/*     */   {
/*  28 */     super(response);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public String encodeRedirectUrl(String url)
/*     */   {
/*  35 */     return encodeRedirectURL(url);
/*     */   }
/*     */ 
/*     */   public String encodeRedirectURL(String url)
/*     */   {
/*  40 */     return addNonce(super.encodeRedirectURL(url));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public String encodeUrl(String url)
/*     */   {
/*  46 */     return encodeURL(url);
/*     */   }
/*     */ 
/*     */   public String encodeURL(String url)
/*     */   {
/*  51 */     return addNonce(super.encodeURL(url));
/*     */   }
/*     */ 
/*     */   private String addNonce(String url) {
/*  55 */     if (url == null) {
/*  56 */       return url;
/*     */     }
/*  58 */     String path = url;
/*  59 */     String query = "";
/*  60 */     String anchor = "";
/*  61 */     int pound = path.indexOf('#');
/*  62 */     if (pound >= 0) {
/*  63 */       anchor = path.substring(pound);
/*  64 */       path = path.substring(0, pound);
/*     */     }
/*  66 */     int question = path.indexOf('?');
/*  67 */     if (question >= 0) {
/*  68 */       query = path.substring(question);
/*  69 */       path = path.substring(0, question);
/*     */     }
/*  71 */     StringBuilder sb = new StringBuilder(path);
/*  72 */     if (query.length() > 0) {
/*  73 */       sb.append(query);
/*  74 */       sb.append('&');
/*     */     } else {
/*  76 */       sb.append('?');
/*     */     }
/*  78 */     sb.append("_xsrf");
/*  79 */     sb.append('=');
/*  80 */     sb.append(generateNonce());
/*  81 */     sb.append(anchor);
/*  82 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private String generateNonce() {
/*  86 */     byte[] random = new byte[16];
/*  87 */     StringBuilder buffer = new StringBuilder(32);
/*  88 */     randomSource.nextBytes(random);
/*  89 */     for (byte element : random) {
/*  90 */       byte b1 = (byte)((element & 0xF0) >> 4);
/*  91 */       byte b2 = (byte)(element & 0xF);
/*  92 */       if (b1 < 10)
/*  93 */         buffer.append((char)(48 + b1));
/*     */       else {
/*  95 */         buffer.append((char)(65 + (b1 - 10)));
/*     */       }
/*  97 */       if (b2 < 10)
/*  98 */         buffer.append((char)(48 + b2));
/*     */       else {
/* 100 */         buffer.append((char)(65 + (b2 - 10)));
/*     */       }
/*     */     }
/* 103 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  16 */       Class clazz = Class.forName(randomClass);
/*  17 */       randomSource = (Random)clazz.newInstance();
/*     */     } catch (ClassNotFoundException e) {
/*  19 */       throw new RuntimeException(e);
/*     */     } catch (InstantiationException e) {
/*  21 */       throw new RuntimeException(e);
/*     */     } catch (IllegalAccessException e) {
/*  23 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\mvn-repo\Central\ooh\bravo\bravo-web-base\1.0.0-SNAPSHOT\bravo-web-base-1.0.0-SNAPSHOT.jar
 * Qualified Name:     ooh.bravo.web.base.wrapper.CsrfResponseWrapper
 * JD-Core Version:    0.6.1
 */