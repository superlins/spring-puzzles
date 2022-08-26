// package org.example.gateway.route;
//
// import org.springframework.data.annotation.Id;
//
// import java.util.Objects;
//
// /**
//  * @author renc
//  */
// public class RouteDefinitionEntity {
//
//     @Id
//     private String id;
//     private String content;
//
//     public String getId() {
//         return id;
//     }
//
//     public void setId(String id) {
//         this.id = id;
//     }
//
//     public String getContent() {
//         return content;
//     }
//
//     public void setContent(String content) {
//         this.content = content;
//     }
//
//     @Override
//     public boolean equals(Object o) {
//         if (this == o) return true;
//         if (o == null || getClass() != o.getClass()) return false;
//
//         RouteDefinitionEntity that = (RouteDefinitionEntity) o;
//
//         if (!Objects.equals(id, that.id)) return false;
//         return Objects.equals(content, that.content);
//     }
//
//     @Override
//     public int hashCode() {
//         int result = id != null ? id.hashCode() : 0;
//         result = 31 * result + (content != null ? content.hashCode() : 0);
//         return result;
//     }
//
//     @Override
//     public String toString() {
//         return "RouteDefinitionEntity{" +
//                 "id='" + id + '\'' +
//                 ", content='" + content + '\'' +
//                 '}';
//     }
// }
