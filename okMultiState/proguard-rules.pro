-keep public class unics.okmultistate.**{
    public *;
    protected * ;
}

# 保留注解
-keepattributes *Annotation*

#以下规则不用添加，R8默认保留了元数据

##保留 kotlin.Metadata 注解从而在保留项目上维持元数据
#-keepattributes RuntimeVisibleAnnotations
#-keep class kotlin.Metadata { *; }

#-keep @kotlin.Metadata public class unics.okmultistate.**{
#        public *;
#        protected *;
#}
#-keepclasseswithmembers @kotlin.Metadata public class unics.okmultistate.** {
#    public *;
#    protected *;
#}
